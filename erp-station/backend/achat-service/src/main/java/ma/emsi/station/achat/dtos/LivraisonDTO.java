package ma.emsi.station.achat.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LivraisonDTO(Long id, LocalDateTime dateLivraison, String numeroBon, Double quantite,
                           BigDecimal prixAchatUnitaire, BigDecimal montantTotal, Long cuveId, String cuveCode,
                           String carburantNom, Long fournisseurId, String fournisseurNom) {
}
