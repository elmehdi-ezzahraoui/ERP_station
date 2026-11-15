package ma.emsi.station.stock.dtos;

import java.math.BigDecimal;

public record CuveDTO(
        Long id,
        String code,
        Double capaciteMax,
        Double volumeActuel,
        Double seuilAlerte,
        Long carburantId,
        String carburantNom,
        BigDecimal prixVente,
        double tauxRemplissage,
        boolean enAlerte) {
}
