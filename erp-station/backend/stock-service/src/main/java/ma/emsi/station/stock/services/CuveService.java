package ma.emsi.station.stock.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.station.stock.dtos.CuveDTO;
import ma.emsi.station.stock.dtos.CuveRequest;
import ma.emsi.station.stock.dtos.EntreeStockRequest;
import ma.emsi.station.stock.dtos.SortieStockRequest;
import ma.emsi.station.stock.entities.Cuve;
import ma.emsi.station.stock.exceptions.BusinessException;
import ma.emsi.station.stock.mappers.StockMapper;
import ma.emsi.station.stock.repositories.CuveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CuveService {

    private final CuveRepository cuveRepository;
    private final CarburantService carburantService;

    @Transactional(readOnly = true)
    public List<CuveDTO> findAll() {
        return cuveRepository.findAll().stream().map(StockMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CuveDTO findById(Long id) {
        return StockMapper.toDto(getEntity(id));
    }

    @Transactional(readOnly = true)
    public List<CuveDTO> findEnAlerte() {
        return cuveRepository.findEnAlerte().stream().map(StockMapper::toDto).toList();
    }

    public Cuve getEntity(Long id) {
        return cuveRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Cuve introuvable : " + id));
    }

    public CuveDTO create(CuveRequest r) {
        if (cuveRepository.existsByCode(r.code())) {
            throw BusinessException.conflict("Une cuve avec le code " + r.code() + " existe deja");
        }
        if (r.volumeActuel() > r.capaciteMax()) {
            throw BusinessException.badRequest("Le volume actuel depasse la capacite de la cuve");
        }
        Cuve cuve = Cuve.builder()
                .code(r.code())
                .capaciteMax(r.capaciteMax())
                .volumeActuel(r.volumeActuel())
                .seuilAlerte(r.seuilAlerte())
                .carburant(carburantService.getEntity(r.carburantId()))
                .build();
        return StockMapper.toDto(cuveRepository.save(cuve));
    }

    public CuveDTO update(Long id, CuveRequest r) {
        Cuve cuve = getEntity(id);
        cuve.setCode(r.code());
        cuve.setCapaciteMax(r.capaciteMax());
        cuve.setSeuilAlerte(r.seuilAlerte());
        cuve.setCarburant(carburantService.getEntity(r.carburantId()));
        return StockMapper.toDto(cuve);
    }

    /** Vente : diminue le stock (appele par vente-service). Refuse si le stock est insuffisant. */
    public CuveDTO sortie(Long id, SortieStockRequest r) {
        Cuve cuve = getEntity(id);
        if (cuve.getVolumeActuel() < r.quantite()) {
            throw BusinessException.conflict(String.format(
                    "Stock insuffisant dans la cuve %s : %.2f L disponibles, %.2f L demandes",
                    cuve.getCode(), cuve.getVolumeActuel(), r.quantite()));
        }
        cuve.setVolumeActuel(cuve.getVolumeActuel() - r.quantite());
        if (cuve.isEnAlerte()) {
            log.warn("ALERTE : cuve {} sous le seuil ({} L restants)", cuve.getCode(), cuve.getVolumeActuel());
        }
        return StockMapper.toDto(cuveRepository.saveAndFlush(cuve));
    }

    /** Livraison : augmente le stock (appele par achat-service). Refuse si la cuve deborde. */
    public CuveDTO entree(Long id, EntreeStockRequest r) {
        Cuve cuve = getEntity(id);
        if (cuve.getVolumeActuel() + r.quantite() > cuve.getCapaciteMax()) {
            throw BusinessException.conflict(String.format(
                    "Capacite depassee pour la cuve %s : %.2f L libres, %.2f L livres",
                    cuve.getCode(), cuve.getCapaciteMax() - cuve.getVolumeActuel(), r.quantite()));
        }
        cuve.setVolumeActuel(cuve.getVolumeActuel() + r.quantite());
        return StockMapper.toDto(cuveRepository.saveAndFlush(cuve));
    }
}
