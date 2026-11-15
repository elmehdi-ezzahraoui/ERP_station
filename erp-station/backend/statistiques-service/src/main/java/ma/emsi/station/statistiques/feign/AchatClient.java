package ma.emsi.station.statistiques.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "achat-service")
public interface AchatClient {

    record PrixAchat(Long carburantId, String carburantNom, Double quantiteTotale, BigDecimal prixAchatMoyen) {
    }

    @GetMapping("/api/livraisons/prix-achat-moyen")
    List<PrixAchat> prixAchatMoyens();
}
