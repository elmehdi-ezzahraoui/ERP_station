package ma.emsi.station.statistiques.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "stock-service")
public interface StockClient {

    record Cuve(Long id, String code, Double capaciteMax, Double volumeActuel, Double seuilAlerte,
                String carburantNom, double tauxRemplissage, boolean enAlerte) {
    }

    record Jaugeage(Long id, LocalDateTime dateReleve, Long cuveId, String cuveCode, Long carburantId,
                    String carburantNom, Double volumeTheorique, Double volumeMesure, Double ecart,
                    double ecartPourcentage, Double toleranceAppliquee, boolean anomalie) {
    }

    @GetMapping("/api/cuves")
    List<Cuve> cuves();

    @GetMapping("/api/jaugeages/periode")
    List<Jaugeage> jaugeages(@RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                             @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin);
}
