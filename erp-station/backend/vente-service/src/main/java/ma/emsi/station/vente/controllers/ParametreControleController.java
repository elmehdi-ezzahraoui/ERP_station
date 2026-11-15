package ma.emsi.station.vente.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.dtos.ParametreControleDTO;
import ma.emsi.station.vente.services.ParametreControleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parametres/controle")
@RequiredArgsConstructor
public class ParametreControleController {

    private final ParametreControleService parametreService;

    @GetMapping
    public ParametreControleDTO get() {
        return parametreService.get();
    }

    @PutMapping
    public ParametreControleDTO update(@Valid @RequestBody ParametreControleDTO dto) {
        return parametreService.update(dto);
    }
}
