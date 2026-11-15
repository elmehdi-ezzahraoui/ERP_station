package ma.emsi.station.authentification.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ma.emsi.station.authentification.enums.Role;

public record UtilisateurRequest(
        @NotBlank String username,
        @NotBlank @Size(min = 6, message = "au moins 6 caracteres") String motDePasse,
        String nomComplet,
        @NotNull Role role) {
}
