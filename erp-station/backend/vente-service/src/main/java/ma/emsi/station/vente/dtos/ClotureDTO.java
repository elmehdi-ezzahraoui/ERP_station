package ma.emsi.station.vente.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ClotureDTO(Long id, Long posteId, String pompiste, LocalDateTime dateCloture, BigDecimal fondCaisse,
                         Double volumeCompteurs, BigDecimal montantCompteurs,
                         BigDecimal totalEspeces, BigDecimal totalCarte, BigDecimal totalCheque, BigDecimal totalCredit,
                         BigDecimal especesAttendues, BigDecimal totalTheorique, BigDecimal totalPhysique,
                         BigDecimal ecart, BigDecimal seuilEcartApplique, boolean ecartHorsTolerance,
                         int nbIndexDiscontinus, List<ReleveIndexDTO> releves) {
}
