package ma.emsi.station.client.mappers;

import ma.emsi.station.client.dtos.ParametreFinancierDTO;
import ma.emsi.station.client.entities.ParametreFinancier;

/**
 * Conversion ParametreFinancier (entite) vers ParametreFinancierDTO.
 */
public final class ParametreMapper {

    private ParametreMapper() {
    }

    public static ParametreFinancierDTO toDto(ParametreFinancier p) {
        return new ParametreFinancierDTO(p.getTauxTva(), p.getSeuilAlerteEncours());
    }
}
