package ma.emsi.station.client.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record FactureDTO(Long id, String numeroFacture, LocalDate dateFacture, LocalDate periodeDebut,
                         LocalDate periodeFin, Long clientId, String raisonSociale, String ice,
                         BigDecimal tauxTva, BigDecimal montantHT, BigDecimal montantTVA, BigDecimal montantTTC,
                         BigDecimal montantRegle, BigDecimal resteAPayer, String statut,
                         List<LigneFactureDTO> lignes) {
}
