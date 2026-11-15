package ma.emsi.station.client.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReglementDTO(Long id, LocalDateTime dateReglement, BigDecimal montant, String modeReglement,
                           String reference, Long factureId, String numeroFacture, String statutFacture) {
}
