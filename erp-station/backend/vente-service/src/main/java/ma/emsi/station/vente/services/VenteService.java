package ma.emsi.station.vente.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.station.vente.dtos.*;
import ma.emsi.station.vente.entities.*;
import ma.emsi.station.vente.enums.ModePaiement;
import ma.emsi.station.vente.exceptions.BusinessException;
import ma.emsi.station.vente.feign.ClientClient.ConsommationRequest;
import ma.emsi.station.vente.feign.ClientClient;
import ma.emsi.station.vente.feign.CuveInfo;
import ma.emsi.station.vente.feign.StockClient.SortieStockRequest;
import ma.emsi.station.vente.feign.StockClient;
import ma.emsi.station.vente.mappers.VenteMapper;
import ma.emsi.station.vente.repositories.VenteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VenteService {

    private final VenteRepository venteRepository;
    private final PisteService pisteService;
    private final PosteService posteService;
    private final StockClient stockClient;
    private final ClientClient clientClient;

    /**
     * Enregistre une vente :
     * 1. recupere le prix courant de la cuve (stock-service)
     * 2. si CREDIT : verifie le plafond du client (client-service) -> blocage si depassement
     * 3. decremente le stock (stock-service)
     * 4. sauvegarde la vente puis, si CREDIT, inscrit la consommation sur le compte client.
     * Une vente en ESPECES/CARTE/CHEQUE n'appelle jamais client-service :
     * une panne de la facturation ne bloque donc pas la piste.
     */
    public VenteDTO enregistrer(VenteRequest r) {
        PosteTravail poste = posteService.getPosteOuvert(r.posteId());
        Pistolet pistolet = pisteService.getPistolet(r.pistoletId());
        if (!pistolet.isActive() || !pistolet.getPompe().isActive()) {
            throw BusinessException.badRequest("Le pistolet ou la pompe est inactif");
        }
        if ((r.volume() == null) == (r.montant() == null)) {
            throw BusinessException.badRequest("Renseigner soit le volume, soit le montant (pas les deux)");
        }

        CuveInfo cuve = appelStock(() -> stockClient.getCuve(pistolet.getCuveId()));
        BigDecimal prix = cuve.prixVente();
        double volume;
        BigDecimal montant;
        if (r.volume() != null) {
            volume = r.volume();
            montant = prix.multiply(BigDecimal.valueOf(volume)).setScale(2, RoundingMode.HALF_UP);
        } else {
            montant = r.montant().setScale(2, RoundingMode.HALF_UP);
            volume = montant.divide(prix, 3, RoundingMode.HALF_UP).doubleValue();
        }

        if (r.modePaiement() == ModePaiement.CREDIT) {
            verifierCredit(r.clientId(), montant);
        }

        appelStock(() -> stockClient.sortie(pistolet.getCuveId(),
                new SortieStockRequest(volume, "VENTE-POSTE-" + poste.getId())));

        Vente vente = venteRepository.save(Vente.builder()
                .dateVente(LocalDateTime.now())
                .volume(volume)
                .prixUnitaire(prix)
                .montantTotal(montant)
                .modePaiement(r.modePaiement())
                .cuveId(pistolet.getCuveId())
                .carburantId(cuve.carburantId())
                .carburantNom(cuve.carburantNom())
                .clientId(r.modePaiement() == ModePaiement.CREDIT ? r.clientId() : null)
                .pistolet(pistolet)
                .poste(poste)
                .build());

        if (r.modePaiement() == ModePaiement.CREDIT) {
            try {
                clientClient.enregistrerConsommation(r.clientId(), new ConsommationRequest(
                        vente.getId(), vente.getDateVente(), vente.getCarburantNom(), volume, prix, montant));
            } catch (FeignException e) {
                // Limite connue : le stock a deja ete decremente. Perspective : pattern Saga / outbox.
                log.error("Echec d'inscription de la vente {} sur le compte client {}", vente.getId(), r.clientId(), e);
                throw BusinessException.unavailable("client-service indisponible : vente a credit non enregistree");
            }
        }
        return VenteMapper.toDto(vente);
    }

    private void verifierCredit(Long clientId, BigDecimal montant) {
        if (clientId == null) {
            throw BusinessException.badRequest("Le client est obligatoire pour une vente a credit");
        }
        ClientClient.AutorisationCredit auto;
        try {
            auto = clientClient.verifierCredit(clientId, montant);
        } catch (FeignException.NotFound e) {
            throw BusinessException.notFound("Client introuvable : " + clientId);
        } catch (FeignException e) {
            throw BusinessException.unavailable(
                    "Vente a credit impossible : client-service indisponible (les autres modes de paiement restent possibles)");
        }
        if (!auto.autorise()) {
            throw new BusinessException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Credit refuse pour " + auto.raisonSociale() + " : " + auto.motif());
        }
    }

    /** Traduit les erreurs de stock-service en erreurs metier lisibles. */
    private <T> T appelStock(java.util.function.Supplier<T> call) {
        try {
            return call.get();
        } catch (FeignException.Conflict e) {
            throw BusinessException.conflict("Stock insuffisant dans la cuve");
        } catch (FeignException.NotFound e) {
            throw BusinessException.notFound("Cuve associee au pistolet introuvable");
        } catch (FeignException e) {
            throw BusinessException.unavailable("stock-service indisponible");
        }
    }

    @Transactional(readOnly = true)
    public List<VenteDTO> findBetween(LocalDate debut, LocalDate fin) {
        return venteRepository
                .findByDateVenteBetweenOrderByDateVenteDesc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(VenteMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<ResumeCarburantDTO> resume(LocalDate debut, LocalDate fin) {
        return venteRepository.resumeParCarburant(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<CaJournalierDTO> caJournalier(LocalDate debut, LocalDate fin) {
        Map<LocalDate, List<Vente>> parJour = venteRepository
                .findByDateVenteBetweenOrderByDateVenteDesc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream()
                .collect(Collectors.groupingBy(v -> v.getDateVente().toLocalDate(), TreeMap::new, Collectors.toList()));
        // jours sans vente inclus pour avoir une courbe continue
        for (LocalDate d = debut; !d.isAfter(fin); d = d.plusDays(1)) {
            parJour.putIfAbsent(d, List.of());
        }
        return parJour.entrySet().stream()
                .map(e -> new CaJournalierDTO(e.getKey(),
                        e.getValue().stream().map(Vente::getMontantTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
                        e.getValue().stream().mapToDouble(Vente::getVolume).sum(),
                        e.getValue().size()))
                .sorted(Comparator.comparing(CaJournalierDTO::date))
                .toList();
    }
}
