package ma.emsi.station.client.dtos;

import java.math.BigDecimal;

public record AutorisationCreditDTO(Long clientId, String raisonSociale, boolean autorise, BigDecimal plafond,
                                    BigDecimal encours, BigDecimal disponible, String motif) {
}
