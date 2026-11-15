package ma.emsi.station.stock.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.stock.dtos.CuveDTO;
import ma.emsi.station.stock.dtos.CuveRequest;
import ma.emsi.station.stock.dtos.EntreeStockRequest;
import ma.emsi.station.stock.dtos.SortieStockRequest;
import ma.emsi.station.stock.services.CuveService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuves")
@RequiredArgsConstructor
public class CuveController {

    private final CuveService cuveService;

    @GetMapping
    public List<CuveDTO> findAll() {
        return cuveService.findAll();
    }

    @GetMapping("/{id}")
    public CuveDTO findById(@PathVariable Long id) {
        return cuveService.findById(id);
    }

    @GetMapping("/alertes")
    public List<CuveDTO> alertes() {
        return cuveService.findEnAlerte();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuveDTO create(@Valid @RequestBody CuveRequest request) {
        return cuveService.create(request);
    }

    @PutMapping("/{id}")
    public CuveDTO update(@PathVariable Long id, @Valid @RequestBody CuveRequest request) {
        return cuveService.update(id, request);
    }

    /** Endpoint interne (bloque par la Gateway) appele par achat-service. */
    @PostMapping("/{id}/entree")
    public CuveDTO entree(@PathVariable Long id, @Valid @RequestBody EntreeStockRequest request) {
        return cuveService.entree(id, request);
    }

    /** Endpoint interne (bloque par la Gateway) appele par vente-service. */
    @PostMapping("/{id}/sortie")
    public CuveDTO sortie(@PathVariable Long id, @Valid @RequestBody SortieStockRequest request) {
        return cuveService.sortie(id, request);
    }
}
