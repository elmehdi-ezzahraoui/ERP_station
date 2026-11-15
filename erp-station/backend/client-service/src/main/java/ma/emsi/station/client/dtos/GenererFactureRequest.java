package ma.emsi.station.client.dtos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GenererFactureRequest(@NotNull Long clientId, @NotNull LocalDate debut, @NotNull LocalDate fin) {
}
