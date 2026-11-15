package ma.emsi.station.vente.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PosteDTO(Long id, String pompiste, LocalDateTime dateOuverture, LocalDateTime dateCloture,
                       BigDecimal fondCaisse, String statut) {
}
