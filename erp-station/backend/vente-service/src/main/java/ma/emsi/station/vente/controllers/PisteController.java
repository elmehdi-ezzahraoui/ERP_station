package ma.emsi.station.vente.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.dtos.*;
import ma.emsi.station.vente.services.PisteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PisteController {

    private final PisteService pisteService;

    @GetMapping("/pompes")
    public List<PompeDTO> pompes() {
        return pisteService.findPompes();
    }

    @PostMapping("/pompes")
    @ResponseStatus(HttpStatus.CREATED)
    public PompeDTO createPompe(@Valid @RequestBody PompeRequest request) {
        return pisteService.createPompe(request);
    }

    @PostMapping("/pompes/{id}/pistolets")
    @ResponseStatus(HttpStatus.CREATED)
    public PistoletDTO addPistolet(@PathVariable Long id, @Valid @RequestBody PistoletRequest request) {
        return pisteService.addPistolet(id, request);
    }

    @GetMapping("/pistolets")
    public List<PistoletDTO> pistolets() {
        return pisteService.findPistolets();
    }
}
