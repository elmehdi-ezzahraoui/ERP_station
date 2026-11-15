package ma.emsi.station.vente.mappers;

import ma.emsi.station.vente.dtos.PistoletDTO;
import ma.emsi.station.vente.dtos.PompeDTO;
import ma.emsi.station.vente.entities.Pistolet;
import ma.emsi.station.vente.entities.Pompe;

/**
 * Conversion Pompe / Pistolet (entites) vers les DTO.
 */
public final class PisteMapper {

    private PisteMapper() {
    }

    public static PompeDTO toDto(Pompe p) {
        return new PompeDTO(p.getId(), p.getNumero(), p.isActive(),
                p.getPistolets().stream().map(PisteMapper::toDto).toList());
    }

    public static PistoletDTO toDto(Pistolet p) {
        return new PistoletDTO(p.getId(), p.getNumero(), p.getCuveId(), p.getPompe().getNumero(),
                p.getDernierIndex(), p.isActive());
    }
}
