package ma.emsi.station.statistiques.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "client-service")
public interface ClientClient {

    record ClientResume(Long id, String raisonSociale, BigDecimal plafondCredit, BigDecimal soldeActuel) {
    }

    record Encours(BigDecimal encoursTotal, BigDecimal impayesFactures, long nombreClients,
                   List<ClientResume> clientsProchesPlafond) {
    }

    record Parametres(BigDecimal tauxTva, BigDecimal seuilAlerteEncours) {
    }

    @GetMapping("/api/clients/encours")
    Encours encours();

    @GetMapping("/api/parametres/financiers")
    Parametres parametres();
}
