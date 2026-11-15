package ma.emsi.station.authentification.mappers;

import ma.emsi.station.authentification.dtos.UtilisateurDTO;
import ma.emsi.station.authentification.entities.Utilisateur;

/**
 * Conversion Utilisateur (entite) vers UtilisateurDTO : le mot de passe n'est jamais renvoye.
 */
public final class UtilisateurMapper {

    private UtilisateurMapper() {
    }

    public static UtilisateurDTO toDto(Utilisateur u) {
        return new UtilisateurDTO(u.getId(), u.getUsername(), u.getNomComplet(), u.getRole(), u.isActif());
    }
}
