package ma.emsi.station.achat.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.achat.dtos.LivraisonDTO;
import ma.emsi.station.achat.dtos.LivraisonRequest;
import ma.emsi.station.achat.dtos.PrixAchatDTO;
import ma.emsi.station.achat.entities.Fournisseur;
import ma.emsi.station.achat.entities.Livraison;
import ma.emsi.station.achat.exceptions.BusinessException;
import ma.emsi.station.achat.feign.StockClient.CuveInfo;
import ma.emsi.station.achat.feign.StockClient.EntreeStockRequest;
import ma.emsi.station.achat.feign.StockClient;
import ma.emsi.station.achat.mappers.AchatMapper;
import ma.emsi.station.achat.repositories.LivraisonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;
    private final FournisseurService fournisseurService;
    private final StockClient stockClient;

    @Transactional(readOnly = true)
    public List<LivraisonDTO> findBetween(LocalDate debut, LocalDate fin) {
        return livraisonRepository
                .findByDateLivraisonBetweenOrderByDateLivraisonDesc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(AchatMapper::toDto).toList();
    }

    /**
     * Reception d'un camion-citerne :
     * 1. verifie la cuve dans stock-service
     * 2. demande a stock-service d'augmenter le stock (refus si la cuve deborde)
     * 3. enregistre la livraison
     */
    public LivraisonDTO receptionner(LivraisonRequest r) {
        Fournisseur fournisseur = fournisseurService.getEntity(r.fournisseurId());
        CuveInfo cuve;
        try {
            cuve = stockClient.getCuve(r.cuveId());
            stockClient.entree(r.cuveId(), new EntreeStockRequest(r.quantite(), "BL-" + r.numeroBon()));
        } catch (FeignException.NotFound e) {
            throw BusinessException.notFound("Cuve introuvable : " + r.cuveId());
        } catch (FeignException.Conflict e) {
            throw BusinessException.conflict("La quantite livree depasse la capacite libre de la cuve");
        } catch (FeignException e) {
            throw BusinessException.unavailable("stock-service indisponible : livraison non enregistree");
        }
        Livraison l = Livraison.builder()
                .dateLivraison(LocalDateTime.now())
                .numeroBon(r.numeroBon())
                .quantite(r.quantite())
                .prixAchatUnitaire(r.prixAchatUnitaire())
                .cuveId(cuve.id())
                .cuveCode(cuve.code())
                .carburantId(cuve.carburantId())
                .carburantNom(cuve.carburantNom())
                .fournisseur(fournisseur)
                .build();
        return AchatMapper.toDto(livraisonRepository.save(l));
    }

    /** Prix d'achat moyen pondere par les quantites livrees, par carburant (consomme par statistiques-service). */
    @Transactional(readOnly = true)
    public List<PrixAchatDTO> prixAchatMoyens() {
        Map<Long, List<Livraison>> parCarburant = livraisonRepository.findAll().stream()
                .collect(Collectors.groupingBy(Livraison::getCarburantId));
        return parCarburant.values().stream().map(livraisons -> {
            double qte = livraisons.stream().mapToDouble(Livraison::getQuantite).sum();
            BigDecimal montant = livraisons.stream()
                    .map(l -> l.getPrixAchatUnitaire().multiply(BigDecimal.valueOf(l.getQuantite())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal moyen = qte == 0 ? BigDecimal.ZERO
                    : montant.divide(BigDecimal.valueOf(qte), 3, RoundingMode.HALF_UP);
            Livraison premiere = livraisons.get(0);
            return new PrixAchatDTO(premiere.getCarburantId(), premiere.getCarburantNom(), qte, moyen);
        }).toList();
    }
}
