package ma.emsi.station.stock.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Demande de decrementation du stock (appelee par vente-service via OpenFeign). */
public record SortieStockRequest(@NotNull @Positive Double quantite, String reference) {
}
