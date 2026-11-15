package ma.emsi.station.statistiques.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record RapportMargesDTO(LocalDate debut, LocalDate fin, List<MargeCarburantDTO> parCarburant,
                               BigDecimal chiffreAffairesHT, BigDecimal margeBruteHT,
                               BigDecimal tauxMargeGlobalPct) {
}
