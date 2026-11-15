package ma.emsi.station.client.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.dtos.*;
import ma.emsi.station.client.services.FacturationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FactureController {

    private final FacturationService facturationService;

    @PostMapping("/factures/generer")
    @ResponseStatus(HttpStatus.CREATED)
    public FactureDTO generer(@Valid @RequestBody GenererFactureRequest request) {
        return facturationService.generer(request);
    }

    @GetMapping("/factures")
    public List<FactureDTO> findAll(@RequestParam(required = false) Long clientId) {
        return facturationService.findAll(clientId);
    }

    @GetMapping("/factures/{id}")
    public FactureDTO findById(@PathVariable Long id) {
        return facturationService.findById(id);
    }

    @PostMapping("/reglements")
    @ResponseStatus(HttpStatus.CREATED)
    public ReglementDTO regler(@Valid @RequestBody ReglementRequest request) {
        return facturationService.regler(request);
    }
}
