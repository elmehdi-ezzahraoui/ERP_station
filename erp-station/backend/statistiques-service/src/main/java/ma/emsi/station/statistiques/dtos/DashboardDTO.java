package ma.emsi.station.statistiques.dtos;

import ma.emsi.station.statistiques.feign.ClientClient.Encours;
import ma.emsi.station.statistiques.feign.StockClient.Cuve;
import ma.emsi.station.statistiques.feign.VenteClient.ResumeCarburant;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Tableau de bord du jour. alertes : messages a afficher au gerant (stock bas, ecart de caisse,
 * coulage, index discontinu). servicesIndisponibles : microservices qui n'ont pas repondu.
 */
public record DashboardDTO(
        LocalDate date,
        BigDecimal chiffreAffaires,
        Double volumeTotal,
        Long nombreVentes,
        List<ResumeCarburant> ventesParCarburant,
        List<Cuve> cuves,
        List<Cuve> cuvesEnAlerte,
        Encours encoursClients,
        List<String> alertes,
        List<String> servicesIndisponibles) {
}
