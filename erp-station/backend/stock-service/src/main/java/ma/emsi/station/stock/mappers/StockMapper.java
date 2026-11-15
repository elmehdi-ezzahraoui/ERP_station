package ma.emsi.station.stock.mappers;

import ma.emsi.station.stock.dtos.CarburantDTO;
import ma.emsi.station.stock.dtos.CuveDTO;
import ma.emsi.station.stock.dtos.JaugeageDTO;
import ma.emsi.station.stock.entities.Carburant;
import ma.emsi.station.stock.entities.Cuve;
import ma.emsi.station.stock.entities.Jaugeage;

/**
 * Conversion des entites du stock vers les DTO.
 */
public final class StockMapper {

    private StockMapper() {
    }

    public static CarburantDTO toDto(Carburant c) {
        return new CarburantDTO(c.getId(), c.getNom(), c.getDensite(), c.getPrixVente(), c.getToleranceCoulagePct());
    }

    public static CuveDTO toDto(Cuve c) {
        double taux = c.getCapaciteMax() == 0 ? 0 : Math.round(c.getVolumeActuel() * 1000 / c.getCapaciteMax()) / 10.0;
        return new CuveDTO(c.getId(), c.getCode(), c.getCapaciteMax(), c.getVolumeActuel(), c.getSeuilAlerte(),
                c.getCarburant().getId(), c.getCarburant().getNom(), c.getCarburant().getPrixVente(),
                taux, c.isEnAlerte());
    }

    public static JaugeageDTO toDto(Jaugeage j) {
        return new JaugeageDTO(j.getId(), j.getDateReleve(), j.getCuve().getId(), j.getCuve().getCode(),
                j.getCuve().getCarburant().getId(), j.getCuve().getCarburant().getNom(),
                j.getVolumeTheorique(), j.getVolumeMesure(), j.getEcart(), ecartPourcentage(j),
                j.getToleranceAppliquee(), j.isAnomalie());
    }

    public static double ecartPourcentage(Jaugeage j) {
        return j.getVolumeTheorique() == 0 ? 0
                : Math.round(j.getEcart() * 10000 / j.getVolumeTheorique()) / 100.0;
    }
}
