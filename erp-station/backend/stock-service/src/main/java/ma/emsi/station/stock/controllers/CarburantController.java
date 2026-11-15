package ma.emsi.station.stock.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.stock.dtos.CarburantDTO;
import ma.emsi.station.stock.services.CarburantService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carburants")
@RequiredArgsConstructor
public class CarburantController {

    private final CarburantService carburantService;

    @GetMapping
    public List<CarburantDTO> findAll() {
        return carburantService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarburantDTO create(@Valid @RequestBody CarburantDTO dto) {
        return carburantService.create(dto);
    }

    @PutMapping("/{id}")
    public CarburantDTO update(@PathVariable Long id, @Valid @RequestBody CarburantDTO dto) {
        return carburantService.update(id, dto);
    }
}
