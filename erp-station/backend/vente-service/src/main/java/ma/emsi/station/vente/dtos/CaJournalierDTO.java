package ma.emsi.station.vente.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CaJournalierDTO(LocalDate date, BigDecimal chiffreAffaires, Double volume, long nombreVentes) {
}
