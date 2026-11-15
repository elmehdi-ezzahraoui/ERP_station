package ma.emsi.station.client.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReglementRequest(@NotNull Long factureId, @NotNull @Positive BigDecimal montant,
                               @NotBlank String modeReglement, String reference) {
}
