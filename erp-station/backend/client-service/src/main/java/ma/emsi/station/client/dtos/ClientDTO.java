package ma.emsi.station.client.dtos;

import java.math.BigDecimal;

public record ClientDTO(Long id, String raisonSociale, String ice, String telephone, String email,
                        String adresse, BigDecimal plafondCredit, BigDecimal soldeActuel,
                        BigDecimal disponible, boolean actif) {
}
