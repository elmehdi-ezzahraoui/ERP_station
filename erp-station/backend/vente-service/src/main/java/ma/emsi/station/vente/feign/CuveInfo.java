package ma.emsi.station.vente.feign;

import java.math.BigDecimal;

/** Vue partielle de la cuve renvoyee par stock-service (seuls les champs utiles sont mappes). */
public record CuveInfo(Long id, String code, Double volumeActuel, Long carburantId, String carburantNom,
                       BigDecimal prixVente, boolean enAlerte) {
}
