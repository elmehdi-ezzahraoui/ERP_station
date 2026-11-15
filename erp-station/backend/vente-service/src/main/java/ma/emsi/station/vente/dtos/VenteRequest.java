package ma.emsi.station.vente.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import ma.emsi.station.vente.enums.ModePaiement;

import java.math.BigDecimal;

/**
 * Saisie d'une vente : le pompiste renseigne soit le volume (litres), soit le montant (DH).
 * clientId est obligatoire pour une vente a CREDIT.
 */
public record VenteRequest(
        @NotNull Long pistoletId,
        @NotNull Long posteId,
        @Positive Double volume,
        @Positive BigDecimal montant,
        @NotNull ModePaiement modePaiement,
        Long clientId) {
}
