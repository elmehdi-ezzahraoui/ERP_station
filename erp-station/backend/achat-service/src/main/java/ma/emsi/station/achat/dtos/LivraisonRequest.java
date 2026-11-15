package ma.emsi.station.achat.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LivraisonRequest(
        @NotNull Long cuveId,
        @NotNull Long fournisseurId,
        @NotNull @Positive Double quantite,
        @NotNull @Positive BigDecimal prixAchatUnitaire,
        String numeroBon) {
}
