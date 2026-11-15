package ma.emsi.station.statistiques.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Synthese des pertes de la station : coulage (cuves) + manquants de caisse (pompistes).
 */
public record RapportPertesDTO(LocalDate debut, LocalDate fin,
                               List<PerteCuveDTO> parCuve,
                               Double perteCarburantLitres, BigDecimal perteCarburantDH,
                               BigDecimal manquantsCaisseDH, BigDecimal totalPertesDH,
                               int nombreAnomaliesJaugeage, int nombreEcartsCaisseHorsTolerance,
                               int nombreIndexDiscontinus) {
}
