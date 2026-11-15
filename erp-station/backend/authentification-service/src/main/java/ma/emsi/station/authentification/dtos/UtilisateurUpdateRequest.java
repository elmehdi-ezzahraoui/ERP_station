package ma.emsi.station.authentification.dtos;

import jakarta.validation.constraints.NotNull;
import ma.emsi.station.authentification.enums.Role;

public record UtilisateurUpdateRequest(String nomComplet, @NotNull Role role, boolean actif) {
}
