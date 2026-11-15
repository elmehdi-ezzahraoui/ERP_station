package ma.emsi.station.stock.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * @param ajusterStock si true, le stock theorique est recale sur le volume mesure.
 */
public record JaugeageRequest(@NotNull Long cuveId, @NotNull @PositiveOrZero Double volumeMesure, boolean ajusterStock) {
}
