package ma.emsi.station.authentification.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangementMotDePasseRequest(
        @NotBlank String ancienMotDePasse,
        @NotBlank @Size(min = 6, message = "au moins 6 caracteres") String nouveauMotDePasse) {
}
