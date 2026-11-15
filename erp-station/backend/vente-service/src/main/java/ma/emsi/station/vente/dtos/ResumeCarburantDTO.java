package ma.emsi.station.vente.dtos;

import java.math.BigDecimal;

public record ResumeCarburantDTO(Long carburantId, String carburantNom, Double volumeTotal,
                                 BigDecimal chiffreAffaires, Long nombreVentes) {
}
