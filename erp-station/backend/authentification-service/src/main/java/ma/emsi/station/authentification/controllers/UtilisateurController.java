package ma.emsi.station.authentification.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.authentification.dtos.UtilisateurDTO;
import ma.emsi.station.authentification.dtos.UtilisateurRequest;
import ma.emsi.station.authentification.dtos.UtilisateurUpdateRequest;
import ma.emsi.station.authentification.services.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    public List<UtilisateurDTO> findAll() {
        return utilisateurService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurDTO create(@Valid @RequestBody UtilisateurRequest request) {
        return utilisateurService.create(request);
    }

    @PutMapping("/{id}")
    public UtilisateurDTO update(@PathVariable Long id, @Valid @RequestBody UtilisateurUpdateRequest request) {
        return utilisateurService.update(id, request);
    }
}
