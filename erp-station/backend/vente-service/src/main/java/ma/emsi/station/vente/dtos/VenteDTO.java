package ma.emsi.station.vente.dtos;

import ma.emsi.station.vente.enums.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VenteDTO(
        Long id,
        LocalDateTime dateVente,
        Integer pompeNumero,
        Integer pistoletNumero,
        String carburantNom,
        Double volume,
        BigDecimal prixUnitaire,
        BigDecimal montantTotal,
        ModePaiement modePaiement,
        Long clientId,
        Long posteId,
        String pompiste) {
}
