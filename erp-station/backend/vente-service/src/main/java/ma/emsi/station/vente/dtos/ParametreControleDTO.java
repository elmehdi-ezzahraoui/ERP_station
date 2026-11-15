package ma.emsi.station.vente.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ParametreControleDTO(@NotNull @PositiveOrZero BigDecimal seuilEcartCaisse, boolean controleContinuiteIndex) {
}
