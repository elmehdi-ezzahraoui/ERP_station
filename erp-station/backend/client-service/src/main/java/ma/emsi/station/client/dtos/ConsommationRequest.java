package ma.emsi.station.client.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsommationRequest(
        @NotNull Long venteId,
        @NotNull LocalDateTime dateVente,
        @NotBlank String carburant,
        @NotNull @Positive Double volume,
        @NotNull @Positive BigDecimal prixUnitaire,
        @NotNull @Positive BigDecimal montantTTC) {
}
