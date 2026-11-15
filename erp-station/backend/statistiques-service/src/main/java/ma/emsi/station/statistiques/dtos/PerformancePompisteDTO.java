package ma.emsi.station.statistiques.dtos;

import java.math.BigDecimal;

/**
 * Bilan d'un pompiste sur la periode. manquantsDH : somme des ecarts negatifs (argent manquant).
 */
public record PerformancePompisteDTO(String pompiste, int nombrePostes, Double volumeVendu,
                                     BigDecimal chiffreAffaires, BigDecimal ecartTotal, BigDecimal manquantsDH,
                                     BigDecimal ecartMoyen, int nombreEcartsHorsTolerance, int nombreIndexDiscontinus) {
}
