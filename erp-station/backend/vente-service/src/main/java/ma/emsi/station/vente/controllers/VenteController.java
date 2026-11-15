package ma.emsi.station.vente.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.dtos.CaJournalierDTO;
import ma.emsi.station.vente.dtos.ResumeCarburantDTO;
import ma.emsi.station.vente.dtos.VenteDTO;
import ma.emsi.station.vente.dtos.VenteRequest;
import ma.emsi.station.vente.services.VenteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@RequiredArgsConstructor
public class VenteController {

    private final VenteService venteService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VenteDTO enregistrer(@Valid @RequestBody VenteRequest request) {
        return venteService.enregistrer(request);
    }

    @GetMapping
    public List<VenteDTO> lister(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return venteService.findBetween(debut != null ? debut : f, f);
    }

    /** Agregat par carburant (volume, CA, nombre de ventes) : consomme par statistiques-service. */
    @GetMapping("/resume")
    public List<ResumeCarburantDTO> resume(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return venteService.resume(debut, fin);
    }

    @GetMapping("/ca-journalier")
    public List<CaJournalierDTO> caJournalier(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return venteService.caJournalier(debut, fin);
    }
}
