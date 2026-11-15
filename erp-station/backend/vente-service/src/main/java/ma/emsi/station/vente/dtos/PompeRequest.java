package ma.emsi.station.vente.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PompeRequest(@NotNull @Positive Integer numero) {
}
