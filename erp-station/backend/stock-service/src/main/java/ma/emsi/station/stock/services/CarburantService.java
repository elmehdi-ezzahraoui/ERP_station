package ma.emsi.station.stock.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.stock.dtos.CarburantDTO;
import ma.emsi.station.stock.entities.Carburant;
import ma.emsi.station.stock.exceptions.BusinessException;
import ma.emsi.station.stock.mappers.StockMapper;
import ma.emsi.station.stock.repositories.CarburantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CarburantService {

    private final CarburantRepository carburantRepository;

    @Transactional(readOnly = true)
    public List<CarburantDTO> findAll() {
        return carburantRepository.findAll().stream().map(StockMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public Carburant getEntity(Long id) {
        return carburantRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Carburant introuvable : " + id));
    }

    public CarburantDTO create(CarburantDTO dto) {
        carburantRepository.findByNomIgnoreCase(dto.nom()).ifPresent(c -> {
            throw BusinessException.conflict("Le carburant " + dto.nom() + " existe deja");
        });
        Carburant c = Carburant.builder().nom(dto.nom()).densite(dto.densite()).prixVente(dto.prixVente())
                .toleranceCoulagePct(dto.toleranceCoulagePct() != null ? dto.toleranceCoulagePct() : 0.5).build();
        return StockMapper.toDto(carburantRepository.save(c));
    }

    /** Changement de prix : les ventes deja faites gardent leur prix (historise dans vente-service). */
    public CarburantDTO update(Long id, CarburantDTO dto) {
        Carburant c = getEntity(id);
        c.setNom(dto.nom());
        c.setDensite(dto.densite());
        c.setPrixVente(dto.prixVente());
        if (dto.toleranceCoulagePct() != null) {
            c.setToleranceCoulagePct(dto.toleranceCoulagePct());
        }
        return StockMapper.toDto(c);
    }
}
