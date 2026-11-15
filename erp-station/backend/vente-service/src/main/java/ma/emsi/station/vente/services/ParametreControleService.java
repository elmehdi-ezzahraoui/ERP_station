package ma.emsi.station.vente.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.dtos.ParametreControleDTO;
import ma.emsi.station.vente.entities.ParametreControle;
import ma.emsi.station.vente.mappers.ParametreMapper;
import ma.emsi.station.vente.repositories.ParametreControleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Parametrage du controle de caisse : ecart tolere et controle de continuite des index. */
@Service
@RequiredArgsConstructor
@Transactional
public class ParametreControleService {

    private static final BigDecimal SEUIL_DEFAUT = new BigDecimal("20.00");

    private final ParametreControleRepository repository;

    public ParametreControle getEntity() {
        return repository.findById(ParametreControle.ID_UNIQUE)
                .orElseGet(() -> repository.save(ParametreControle.builder()
                        .id(ParametreControle.ID_UNIQUE)
                        .seuilEcartCaisse(SEUIL_DEFAUT)
                        .controleContinuiteIndex(true)
                        .build()));
    }

    public ParametreControleDTO get() {
        return ParametreMapper.toDto(getEntity());
    }

    public ParametreControleDTO update(ParametreControleDTO dto) {
        ParametreControle p = getEntity();
        p.setSeuilEcartCaisse(dto.seuilEcartCaisse());
        p.setControleContinuiteIndex(dto.controleContinuiteIndex());
        return ParametreMapper.toDto(p);
    }
}
