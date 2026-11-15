package ma.emsi.station.vente.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.List;

/**
 * Cloture du poste : argent compte en caisse + index de fin de chaque pistolet releve a l'ouverture.
 */
public record ClotureRequest(@NotNull @PositiveOrZero BigDecimal totalPhysique,
                             List<@Valid IndexPistoletRequest> indexFin) {
}
