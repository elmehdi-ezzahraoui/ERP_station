package ma.emsi.station.achat.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.achat.dtos.FournisseurDTO;
import ma.emsi.station.achat.services.FournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
@RequiredArgsConstructor
public class FournisseurController {

    private final FournisseurService fournisseurService;

    @GetMapping
    public List<FournisseurDTO> findAll() {
        return fournisseurService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FournisseurDTO create(@Valid @RequestBody FournisseurDTO dto) {
        return fournisseurService.create(dto);
    }

    @PutMapping("/{id}")
    public FournisseurDTO update(@PathVariable Long id, @Valid @RequestBody FournisseurDTO dto) {
        return fournisseurService.update(id, dto);
    }
}
