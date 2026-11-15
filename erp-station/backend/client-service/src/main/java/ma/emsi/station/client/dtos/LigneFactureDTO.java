package ma.emsi.station.client.dtos;

import java.math.BigDecimal;

public record LigneFactureDTO(String designation, Double quantite, BigDecimal prixUnitaireHT,
                              BigDecimal montantHT, BigDecimal montantTTC) {
}
