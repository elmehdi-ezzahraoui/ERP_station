package ma.emsi.station.vente.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record IndexPistoletRequest(@NotNull Long pistoletId, @NotNull @PositiveOrZero Double index) {
}
