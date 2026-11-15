package ma.emsi.station.statistiques.dtos;

import java.math.BigDecimal;

public record MargeCarburantDTO(String carburant, Double volumeVendu, BigDecimal chiffreAffairesTTC,
                                BigDecimal chiffreAffairesHT, BigDecimal prixAchatMoyenHT,
                                BigDecimal coutAchatHT, BigDecimal margeBruteHT, BigDecimal tauxMargePct) {
}
