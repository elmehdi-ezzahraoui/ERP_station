package ma.emsi.station.stock.dtos;

import java.time.LocalDateTime;

public record JaugeageDTO(
        Long id,
        LocalDateTime dateReleve,
        Long cuveId,
        String cuveCode,
        Long carburantId,
        String carburantNom,
        Double volumeTheorique,
        Double volumeMesure,
        Double ecart,
        double ecartPourcentage,
        Double toleranceAppliquee,
        boolean anomalie) {
}
