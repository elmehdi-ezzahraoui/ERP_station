package ma.emsi.station.stock.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CuveRequest(
        @NotBlank String code,
        @NotNull @Positive Double capaciteMax,
        @NotNull @PositiveOrZero Double volumeActuel,
        @NotNull @PositiveOrZero Double seuilAlerte,
        @NotNull Long carburantId) {
}
