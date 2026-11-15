package ma.emsi.station.stock.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.stock.dtos.JaugeageDTO;
import ma.emsi.station.stock.dtos.JaugeageRequest;
import ma.emsi.station.stock.services.JaugeageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/jaugeages")
@RequiredArgsConstructor
public class JaugeageController {

    private final JaugeageService jaugeageService;

    @GetMapping
    public List<JaugeageDTO> findByCuve(@RequestParam Long cuveId) {
        return jaugeageService.findByCuve(cuveId);
    }

    /** Tous les jaugeages d'une periode (consomme par statistiques-service). */
    @GetMapping("/periode")
    public List<JaugeageDTO> findBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return jaugeageService.findBetween(debut, fin);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JaugeageDTO jauger(@Valid @RequestBody JaugeageRequest request) {
        return jaugeageService.jauger(request);
    }
}
