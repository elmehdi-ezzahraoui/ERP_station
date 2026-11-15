package ma.emsi.station.achat.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.achat.dtos.LivraisonDTO;
import ma.emsi.station.achat.dtos.LivraisonRequest;
import ma.emsi.station.achat.dtos.PrixAchatDTO;
import ma.emsi.station.achat.services.LivraisonService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    @GetMapping
    public List<LivraisonDTO> findBetween(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return livraisonService.findBetween(debut != null ? debut : f.minusDays(30), f);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivraisonDTO receptionner(@Valid @RequestBody LivraisonRequest request) {
        return livraisonService.receptionner(request);
    }

    @GetMapping("/prix-achat-moyen")
    public List<PrixAchatDTO> prixAchatMoyens() {
        return livraisonService.prixAchatMoyens();
    }
}
