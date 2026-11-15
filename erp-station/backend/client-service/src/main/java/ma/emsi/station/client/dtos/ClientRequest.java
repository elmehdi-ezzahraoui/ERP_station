package ma.emsi.station.client.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ClientRequest(
        @NotBlank String raisonSociale,
        @Pattern(regexp = "\\d{15}", message = "l'ICE doit contenir 15 chiffres") String ice,
        String telephone,
        @Email String email,
        String adresse,
        @NotNull @PositiveOrZero BigDecimal plafondCredit,
        Boolean actif) {
}
