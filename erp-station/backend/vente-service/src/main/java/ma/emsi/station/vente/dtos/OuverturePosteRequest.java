package ma.emsi.station.vente.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/** pompiste est optionnel : par defaut l'utilisateur connecte (en-tete X-User). */
public record OuverturePosteRequest(String pompiste, @NotNull @PositiveOrZero BigDecimal fondCaisse) {
}
