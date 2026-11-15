package ma.emsi.station.vente.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.dtos.*;
import ma.emsi.station.vente.services.PosteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PosteController {

    private final PosteService posteService;

    /** X-User est ajoute par la Gateway a partir du JWT. */
    @PostMapping("/postes/ouvrir")
    @ResponseStatus(HttpStatus.CREATED)
    public PosteDTO ouvrir(@Valid @RequestBody OuverturePosteRequest request,
                           @RequestHeader(value = "X-User", required = false) String utilisateur) {
        return posteService.ouvrir(request, utilisateur);
    }

    @PostMapping("/postes/{id}/index-debut")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ReleveIndexDTO> indexDebut(@PathVariable Long id, @Valid @RequestBody ReleveIndexRequest request) {
        return posteService.saisirIndexDebut(id, request);
    }

    @GetMapping("/postes/{id}/releves")
    public List<ReleveIndexDTO> releves(@PathVariable Long id) {
        return posteService.releves(id);
    }

    @GetMapping("/postes/ouverts")
    public List<PosteDTO> ouverts() {
        return posteService.findOuverts();
    }

    @PostMapping("/postes/{id}/cloturer")
    public ClotureDTO cloturer(@PathVariable Long id, @Valid @RequestBody ClotureRequest request) {
        return posteService.cloturer(id, request);
    }

    @GetMapping("/clotures")
    public List<ClotureDTO> clotures(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        LocalDate f = fin != null ? fin : LocalDate.now();
        return posteService.findClotures(debut != null ? debut : f.minusDays(7), f);
    }
}
