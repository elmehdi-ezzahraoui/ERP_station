package ma.emsi.station.client.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.dtos.*;
import ma.emsi.station.client.services.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public List<ClientDTO> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public ClientDTO findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO create(@Valid @RequestBody ClientRequest request) {
        return clientService.create(request);
    }

    @PutMapping("/{id}")
    public ClientDTO update(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return clientService.update(id, request);
    }

    @GetMapping("/{id}/autorisation")
    public AutorisationCreditDTO autorisation(@PathVariable Long id, @RequestParam BigDecimal montant) {
        return clientService.verifierCredit(id, montant);
    }

    /** Endpoint interne (bloque par la Gateway) appele par vente-service. */
    @PostMapping("/{id}/consommations")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsommationDTO enregistrerConsommation(@PathVariable Long id,
                                                   @Valid @RequestBody ConsommationRequest request) {
        return clientService.enregistrerConsommation(id, request);
    }

    @GetMapping("/{id}/consommations")
    public List<ConsommationDTO> consommations(@PathVariable Long id) {
        return clientService.consommations(id);
    }

    @GetMapping("/encours")
    public EncoursDTO encours() {
        return clientService.encours();
    }
}
