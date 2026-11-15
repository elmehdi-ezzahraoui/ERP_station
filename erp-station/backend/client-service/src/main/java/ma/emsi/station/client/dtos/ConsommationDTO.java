package ma.emsi.station.client.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsommationDTO(Long id, Long venteId, LocalDateTime dateVente, String carburant, Double volume,
                              BigDecimal prixUnitaire, BigDecimal montantTTC, String numeroFacture) {
}
