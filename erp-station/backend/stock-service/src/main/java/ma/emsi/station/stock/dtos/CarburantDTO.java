package ma.emsi.station.stock.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** toleranceCoulagePct : ecart tolere au jaugeage, en % (0.5 par defaut). */
public record CarburantDTO(
        Long id,
        @NotBlank String nom,
        Double densite,
        @NotNull @Positive BigDecimal prixVente,
        @PositiveOrZero @DecimalMax("10") Double toleranceCoulagePct) {
}
