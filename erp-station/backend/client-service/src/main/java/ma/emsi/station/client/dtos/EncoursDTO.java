package ma.emsi.station.client.dtos;

import java.math.BigDecimal;
import java.util.List;

/** Synthese des encours clients (consommee par statistiques-service). */
public record EncoursDTO(BigDecimal encoursTotal, BigDecimal impayesFactures, long nombreClients,
                         List<ClientDTO> clientsProchesPlafond) {
}
