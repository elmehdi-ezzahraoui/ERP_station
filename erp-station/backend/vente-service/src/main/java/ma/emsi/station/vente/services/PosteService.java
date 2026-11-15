package ma.emsi.station.vente.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.station.vente.dtos.*;
import ma.emsi.station.vente.entities.*;
import ma.emsi.station.vente.enums.ModePaiement;
import ma.emsi.station.vente.enums.StatutPoste;
import ma.emsi.station.vente.exceptions.BusinessException;
import ma.emsi.station.vente.feign.CuveInfo;
import ma.emsi.station.vente.feign.StockClient.SortieStockRequest;
import ma.emsi.station.vente.feign.StockClient;
import ma.emsi.station.vente.mappers.CaisseMapper;
import ma.emsi.station.vente.repositories.ClotureCaisseRepository;
import ma.emsi.station.vente.repositories.PosteTravailRepository;
import ma.emsi.station.vente.repositories.ReleveIndexRepository;
import ma.emsi.station.vente.repositories.VenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Poste du pompiste : ouverture, releve des index de debut, cloture avec controle des ecarts.
 *
 * Principe (comme dans les stations marocaines) : le volume vendu est donne par les compteurs
 * (index fin - index debut). Les ventes carte, cheque et credit sont saisies une par une ;
 * le reste du volume compteur est compte comme vente en especes a la cloture.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PosteService {

    private static final double EPSILON = 0.0005;

    private final PosteTravailRepository posteRepository;
    private final ClotureCaisseRepository clotureRepository;
    private final VenteRepository venteRepository;
    private final ReleveIndexRepository releveRepository;
    private final PisteService pisteService;
    private final ParametreControleService parametreService;
    private final StockClient stockClient;

    public PosteDTO ouvrir(OuverturePosteRequest r, String utilisateur) {
        String pompiste = r.pompiste() != null && !r.pompiste().isBlank() ? r.pompiste() : utilisateur;
        if (pompiste == null || pompiste.isBlank()) {
            throw BusinessException.badRequest("Pompiste non identifie");
        }
        if (posteRepository.existsByPompisteAndStatut(pompiste, StatutPoste.OUVERT)) {
            throw BusinessException.conflict(pompiste + " a deja un poste ouvert");
        }
        PosteTravail p = posteRepository.save(PosteTravail.builder()
                .pompiste(pompiste)
                .dateOuverture(LocalDateTime.now())
                .fondCaisse(r.fondCaisse())
                .statut(StatutPoste.OUVERT)
                .build());
        return CaisseMapper.toDto(p);
    }

    /** Releve des index de debut. Signale un index qui ne suit pas la fin du poste precedent. */
    public List<ReleveIndexDTO> saisirIndexDebut(Long posteId, ReleveIndexRequest r) {
        PosteTravail poste = getPosteOuvert(posteId);
        ParametreControle param = parametreService.getEntity();
        List<ReleveIndexDTO> resultat = new ArrayList<>();
        for (IndexPistoletRequest i : r.releves()) {
            Pistolet pistolet = pisteService.getPistolet(i.pistoletId());
            if (releveRepository.existsByPosteIdAndPistoletId(posteId, pistolet.getId())) {
                throw BusinessException.conflict("Index de debut deja saisi pour le pistolet " + pistolet.getId());
            }
            if (releveRepository.existsByPistoletIdAndIndexFinIsNull(pistolet.getId())) {
                throw BusinessException.conflict("Le pistolet " + pistolet.getId() + " est deja utilise dans un autre poste ouvert");
            }
            double dernier = pistolet.getDernierIndex() != null ? pistolet.getDernierIndex() : 0.0;
            boolean discontinu = param.isControleContinuiteIndex() && Math.abs(i.index() - dernier) > 0.01;
            if (discontinu) {
                log.warn("ANOMALIE : pistolet {} index de debut {} different du dernier index connu {}",
                        pistolet.getId(), i.index(), dernier);
            }
            ReleveIndex releve = releveRepository.save(ReleveIndex.builder()
                    .poste(poste)
                    .pistolet(pistolet)
                    .dernierIndexConnu(dernier)
                    .indexDebut(i.index())
                    .indexDiscontinu(discontinu)
                    .build());
            resultat.add(CaisseMapper.toDto(releve));
        }
        return resultat;
    }

    @Transactional(readOnly = true)
    public List<ReleveIndexDTO> releves(Long posteId) {
        return releveRepository.findByPosteIdOrderById(posteId).stream().map(CaisseMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PosteDTO> findOuverts() {
        return posteRepository.findByStatut(StatutPoste.OUVERT).stream().map(CaisseMapper::toDto).toList();
    }

    public PosteTravail getPosteOuvert(Long id) {
        PosteTravail p = posteRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Poste introuvable : " + id));
        if (p.getStatut() != StatutPoste.OUVERT) {
            throw BusinessException.badRequest("Le poste " + id + " est deja cloture");
        }
        return p;
    }

    /**
     * Cloture du poste :
     * 1. pour chaque pistolet : volume compteur = index fin - index debut, valorise au prix de vente
     * 2. le volume non saisi (compteur - ventes deja saisies) devient une vente en especes et sort du stock
     * 3. especes attendues = montant compteurs - carte - cheque - credit
     * 4. ecart = argent compte - (fond de caisse + especes attendues), compare au seuil parametre
     * Sans releve d'index, on revient au calcul simple : fond + ventes especes saisies.
     */
    public ClotureDTO cloturer(Long posteId, ClotureRequest r) {
        PosteTravail poste = getPosteOuvert(posteId);
        ParametreControle param = parametreService.getEntity();
        List<ReleveIndex> releves = releveRepository.findByPosteIdOrderById(posteId);

        double volumeCompteurs = 0.0;
        BigDecimal montantCompteurs = BigDecimal.ZERO;
        int discontinus = 0;

        if (!releves.isEmpty()) {
            Map<Long, Double> indexFin = r.indexFin() == null ? Map.of() : r.indexFin().stream()
                    .collect(Collectors.toMap(IndexPistoletRequest::pistoletId, IndexPistoletRequest::index, (a, b) -> b));
            for (ReleveIndex releve : releves) {
                Pistolet pistolet = releve.getPistolet();
                Double fin = indexFin.get(pistolet.getId());
                if (fin == null) {
                    throw BusinessException.badRequest("Index de fin manquant pour le pistolet " + pistolet.getId());
                }
                if (fin < releve.getIndexDebut()) {
                    throw BusinessException.badRequest("L'index de fin du pistolet " + pistolet.getId()
                            + " est inferieur a son index de debut");
                }
                CuveInfo cuve = appelStock(() -> stockClient.getCuve(pistolet.getCuveId()));
                double volume = arrondi(fin - releve.getIndexDebut());
                BigDecimal montant = cuve.prixVente().multiply(BigDecimal.valueOf(volume)).setScale(2, RoundingMode.HALF_UP);

                releve.setIndexFin(fin);
                releve.setVolumeCompteur(volume);
                releve.setPrixUnitaire(cuve.prixVente());
                releve.setMontantCompteur(montant);
                pistolet.setDernierIndex(fin);

                enregistrerVenteEspecesNonSaisie(poste, pistolet, cuve, volume);

                volumeCompteurs += volume;
                montantCompteurs = montantCompteurs.add(montant);
                if (releve.isIndexDiscontinu()) {
                    discontinus++;
                }
            }
        }

        BigDecimal especes = total(posteId, ModePaiement.ESPECES);
        BigDecimal carte = total(posteId, ModePaiement.CARTE);
        BigDecimal cheque = total(posteId, ModePaiement.CHEQUE);
        BigDecimal credit = total(posteId, ModePaiement.CREDIT);
        BigDecimal especesAttendues = releves.isEmpty() ? especes
                : montantCompteurs.subtract(carte).subtract(cheque).subtract(credit);
        BigDecimal theorique = poste.getFondCaisse().add(especesAttendues);
        BigDecimal ecart = r.totalPhysique().subtract(theorique);
        boolean horsTolerance = ecart.abs().compareTo(param.getSeuilEcartCaisse()) > 0;
        if (horsTolerance) {
            log.warn("ANOMALIE : ecart de caisse de {} DH pour {} (poste {})", ecart, poste.getPompiste(), posteId);
        }

        poste.setStatut(StatutPoste.CLOTURE);
        poste.setDateCloture(LocalDateTime.now());

        ClotureCaisse c = clotureRepository.save(ClotureCaisse.builder()
                .dateCloture(poste.getDateCloture())
                .volumeCompteurs(arrondi(volumeCompteurs))
                .montantCompteurs(montantCompteurs)
                .totalEspeces(especes)
                .totalCarte(carte)
                .totalCheque(cheque)
                .totalCredit(credit)
                .especesAttendues(especesAttendues)
                .totalTheorique(theorique)
                .totalPhysique(r.totalPhysique())
                .ecart(ecart)
                .seuilEcartApplique(param.getSeuilEcartCaisse())
                .ecartHorsTolerance(horsTolerance)
                .nbIndexDiscontinus(discontinus)
                .poste(poste)
                .build());
        return CaisseMapper.toDto(c, releves);
    }

    /** Le volume compteur non couvert par des ventes saisies est une vente especes : on l'enregistre et on sort le stock. */
    private void enregistrerVenteEspecesNonSaisie(PosteTravail poste, Pistolet pistolet, CuveInfo cuve, double volumeCompteur) {
        Double saisi = venteRepository.volumeSaisi(poste.getId(), pistolet.getId());
        double reste = arrondi(volumeCompteur - (saisi != null ? saisi : 0.0));
        if (reste < -EPSILON) {
            log.warn("ANOMALIE : ventes saisies superieures au compteur pour le pistolet {} (poste {})",
                    pistolet.getId(), poste.getId());
            return;
        }
        if (reste <= EPSILON) {
            return;
        }
        appelStock(() -> stockClient.sortie(pistolet.getCuveId(),
                new SortieStockRequest(reste, "CLOTURE-POSTE-" + poste.getId())));
        venteRepository.save(Vente.builder()
                .dateVente(LocalDateTime.now())
                .volume(reste)
                .prixUnitaire(cuve.prixVente())
                .montantTotal(cuve.prixVente().multiply(BigDecimal.valueOf(reste)).setScale(2, RoundingMode.HALF_UP))
                .modePaiement(ModePaiement.ESPECES)
                .cuveId(pistolet.getCuveId())
                .carburantId(cuve.carburantId())
                .carburantNom(cuve.carburantNom())
                .calculeeParIndex(true)
                .pistolet(pistolet)
                .poste(poste)
                .build());
    }

    private BigDecimal total(Long posteId, ModePaiement mode) {
        BigDecimal t = venteRepository.totalParMode(posteId, mode);
        return t != null ? t : BigDecimal.ZERO;
    }

    private static double arrondi(double litres) {
        return Math.round(litres * 1000) / 1000.0;
    }

    private <T> T appelStock(Supplier<T> call) {
        try {
            return call.get();
        } catch (FeignException.Conflict e) {
            throw BusinessException.conflict("Stock insuffisant dans la cuve : verifier les index ou faire un jaugeage");
        } catch (FeignException.NotFound e) {
            throw BusinessException.notFound("Cuve associee au pistolet introuvable");
        } catch (FeignException e) {
            throw BusinessException.unavailable("stock-service indisponible : cloture impossible pour le moment");
        }
    }

    @Transactional(readOnly = true)
    public List<ClotureDTO> findClotures(LocalDate debut, LocalDate fin) {
        return clotureRepository
                .findByDateClotureBetweenOrderByDateClotureDesc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream()
                .map(c -> CaisseMapper.toDto(c, releveRepository.findByPosteIdOrderById(c.getPoste().getId())))
                .toList();
    }
}
