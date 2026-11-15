package ma.emsi.station.client.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.dtos.ParametreFinancierDTO;
import ma.emsi.station.client.services.ParametreService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parametres/financiers")
@RequiredArgsConstructor
public class ParametreController {

    private final ParametreService parametreService;

    @GetMapping
    public ParametreFinancierDTO get() {
        return parametreService.get();
    }

    @PutMapping
    public ParametreFinancierDTO update(@Valid @RequestBody ParametreFinancierDTO dto) {
        return parametreService.update(dto);
    }
}
