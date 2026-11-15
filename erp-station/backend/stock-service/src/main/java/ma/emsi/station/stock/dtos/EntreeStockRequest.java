package ma.emsi.station.stock.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** Demande d'augmentation du stock (appelee par achat-service via OpenFeign). */
public record EntreeStockRequest(@NotNull @Positive Double quantite, String reference) {
}
