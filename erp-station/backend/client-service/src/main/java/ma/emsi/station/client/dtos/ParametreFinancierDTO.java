package ma.emsi.station.client.dtos;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ParametreFinancierDTO(
        @NotNull @DecimalMin("0") @DecimalMax("1") BigDecimal tauxTva,
        @NotNull @DecimalMin("0") @DecimalMax("1") BigDecimal seuilAlerteEncours) {
}
