package ma.emsi.station.vente.mappers;

import ma.emsi.station.vente.dtos.ParametreControleDTO;
import ma.emsi.station.vente.entities.ParametreControle;

/**
 * Conversion ParametreControle (entite) vers ParametreControleDTO.
 */
public final class ParametreMapper {

    private ParametreMapper() {
    }

    public static ParametreControleDTO toDto(ParametreControle p) {
        return new ParametreControleDTO(p.getSeuilEcartCaisse(), p.isControleContinuiteIndex());
    }
}
