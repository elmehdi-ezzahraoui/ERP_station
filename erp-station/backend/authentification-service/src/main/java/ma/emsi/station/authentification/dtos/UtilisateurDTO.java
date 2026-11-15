package ma.emsi.station.authentification.dtos;

import ma.emsi.station.authentification.enums.Role;

public record UtilisateurDTO(Long id, String username, String nomComplet, Role role, boolean actif) {
}
