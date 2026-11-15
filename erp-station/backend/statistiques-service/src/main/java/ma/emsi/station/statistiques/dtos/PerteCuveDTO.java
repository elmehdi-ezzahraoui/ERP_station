package ma.emsi.station.statistiques.dtos;

import java.math.BigDecimal;

/** Ecarts de jaugeage d'une cuve sur la periode, en litres et en dirhams (au prix d'achat moyen). */
public record PerteCuveDTO(String cuveCode, String carburantNom, int nombreJaugeages, int nombreAnomalies,
                           Double ecartLitres, BigDecimal ecartValeurDH, Double pireEcartPct, Double tolerancePct) {
}
