package ma.emsi.station.statistiques.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "vente-service")
public interface VenteClient {

    record ResumeCarburant(Long carburantId, String carburantNom, Double volumeTotal,
                           BigDecimal chiffreAffaires, Long nombreVentes) {
    }

    record CaJournalier(LocalDate date, BigDecimal chiffreAffaires, Double volume, long nombreVentes) {
    }

    /** Vue partielle d'une cloture de caisse. */
    record Cloture(Long id, Long posteId, String pompiste, LocalDateTime dateCloture, Double volumeCompteurs,
                   BigDecimal montantCompteurs, BigDecimal totalTheorique, BigDecimal totalPhysique, BigDecimal ecart,
                   BigDecimal seuilEcartApplique, boolean ecartHorsTolerance, int nbIndexDiscontinus) {
    }

    @GetMapping("/api/ventes/resume")
    List<ResumeCarburant> resume(@RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                                 @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin);

    @GetMapping("/api/ventes/ca-journalier")
    List<CaJournalier> caJournalier(@RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                                    @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin);

    @GetMapping("/api/clotures")
    List<Cloture> clotures(@RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
                           @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin);
}
