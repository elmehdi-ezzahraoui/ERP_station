package ma.emsi.station.achat.dtos;

import java.math.BigDecimal;

/** Prix d'achat moyen pondere (DH HT / litre) d'un carburant, utilise pour le calcul des marges. */
public record PrixAchatDTO(Long carburantId, String carburantNom, Double quantiteTotale, BigDecimal prixAchatMoyen) {
}
