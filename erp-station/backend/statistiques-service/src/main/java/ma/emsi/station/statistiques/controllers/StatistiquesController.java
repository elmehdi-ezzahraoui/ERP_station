package ma.emsi.station.statistiques.controllers;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.statistiques.dtos.DashboardDTO;
import ma.emsi.station.statistiques.dtos.PerformancePompisteDTO;
import ma.emsi.station.statistiques.dtos.RapportMargesDTO;
import ma.emsi.station.statistiques.dtos.RapportPertesDTO;
import ma.emsi.station.statistiques.feign.VenteClient.CaJournalier;
import ma.emsi.station.statistiques.services.StatistiquesService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
public class StatistiquesController {

    private final StatistiquesService service;

    @GetMapping("/dashboard")
    public DashboardDTO dashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return service.dashboard(date != null ? date : LocalDate.now());
    }

    @GetMapping("/ca")
    public List<CaJournalier> evolutionCa(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return service.evolutionCa(debut != null ? debut : f.minusDays(29), f);
    }

    @GetMapping("/marges")
    public RapportMargesDTO marges(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return service.marges(debut != null ? debut : f.withDayOfMonth(1), f);
    }

    /** Pertes de carburant (coulage) et d'argent (manquants de caisse), en litres et en DH. */
    @GetMapping("/pertes")
    public RapportPertesDTO pertes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return service.pertes(debut != null ? debut : f.withDayOfMonth(1), f);
    }

    /** Bilan par pompiste (ecarts, manquants, index discontinus), du plus gros manquant au plus petit. */
    @GetMapping("/pompistes")
    public List<PerformancePompisteDTO> pompistes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return service.pompistes(debut != null ? debut : f.withDayOfMonth(1), f);
    }
}
