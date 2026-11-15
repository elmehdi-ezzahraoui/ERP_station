package ma.emsi.station.vente.services;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.dtos.*;
import ma.emsi.station.vente.entities.Pistolet;
import ma.emsi.station.vente.entities.Pompe;
import ma.emsi.station.vente.exceptions.BusinessException;
import ma.emsi.station.vente.feign.StockClient;
import ma.emsi.station.vente.mappers.PisteMapper;
import ma.emsi.station.vente.repositories.PistoletRepository;
import ma.emsi.station.vente.repositories.PompeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Parametrage de la piste : pompes et pistolets. */
@Service
@RequiredArgsConstructor
@Transactional
public class PisteService {

    private final PompeRepository pompeRepository;
    private final PistoletRepository pistoletRepository;
    private final StockClient stockClient;

    @Transactional(readOnly = true)
    public List<PompeDTO> findPompes() {
        return pompeRepository.findAll().stream().map(PisteMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<PistoletDTO> findPistolets() {
        return pistoletRepository.findAll().stream().map(PisteMapper::toDto).toList();
    }

    public PompeDTO createPompe(PompeRequest r) {
        if (pompeRepository.existsByNumero(r.numero())) {
            throw BusinessException.conflict("La pompe n." + r.numero() + " existe deja");
        }
        return PisteMapper.toDto(pompeRepository.save(Pompe.builder().numero(r.numero()).build()));
    }

    public PistoletDTO addPistolet(Long pompeId, PistoletRequest r) {
        Pompe pompe = pompeRepository.findById(pompeId)
                .orElseThrow(() -> BusinessException.notFound("Pompe introuvable : " + pompeId));
        // Verification applicative de l'integrite referentielle (pas de FK entre bases)
        try {
            stockClient.getCuve(r.cuveId());
        } catch (FeignException.NotFound e) {
            throw BusinessException.badRequest("La cuve " + r.cuveId() + " n'existe pas dans stock-service");
        } catch (FeignException e) {
            throw BusinessException.unavailable("stock-service indisponible, impossible de verifier la cuve");
        }
        Pistolet p = Pistolet.builder().numero(r.numero()).cuveId(r.cuveId())
                .dernierIndex(r.indexInitial() != null ? r.indexInitial() : 0.0).pompe(pompe).build();
        pompe.getPistolets().add(p);
        return PisteMapper.toDto(pistoletRepository.save(p));
    }

    public Pistolet getPistolet(Long id) {
        return pistoletRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Pistolet introuvable : " + id));
    }
}
