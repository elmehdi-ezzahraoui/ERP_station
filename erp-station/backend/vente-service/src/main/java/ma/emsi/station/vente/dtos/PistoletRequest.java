package ma.emsi.station.vente.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/** indexInitial : valeur actuelle du compteur du pistolet au moment de son installation dans le logiciel. */
public record PistoletRequest(@NotNull @Positive Integer numero, @NotNull Long cuveId, @PositiveOrZero Double indexInitial) {
}
