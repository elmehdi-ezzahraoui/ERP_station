package ma.emsi.station.client.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.dtos.ParametreFinancierDTO;
import ma.emsi.station.client.entities.ParametreFinancier;
import ma.emsi.station.client.mappers.ParametreMapper;
import ma.emsi.station.client.repositories.ParametreFinancierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/** Parametrage financier : TVA et seuil d'alerte des encours, modifiables par l'administrateur / le comptable. */
@Service
@RequiredArgsConstructor
@Transactional
public class ParametreService {

    private static final BigDecimal TVA_DEFAUT = new BigDecimal("0.10");
    private static final BigDecimal SEUIL_DEFAUT = new BigDecimal("0.80");

    private final ParametreFinancierRepository repository;

    public ParametreFinancier getEntity() {
        return repository.findById(ParametreFinancier.ID_UNIQUE)
                .orElseGet(() -> repository.save(ParametreFinancier.builder()
                        .id(ParametreFinancier.ID_UNIQUE)
                        .tauxTva(TVA_DEFAUT)
                        .seuilAlerteEncours(SEUIL_DEFAUT)
                        .build()));
    }

    public ParametreFinancierDTO get() {
        return ParametreMapper.toDto(getEntity());
    }

    public ParametreFinancierDTO update(ParametreFinancierDTO dto) {
        ParametreFinancier p = getEntity();
        p.setTauxTva(dto.tauxTva());
        p.setSeuilAlerteEncours(dto.seuilAlerteEncours());
        return ParametreMapper.toDto(p);
    }

    public BigDecimal getTauxTva() {
        return getEntity().getTauxTva();
    }

    public BigDecimal getSeuilAlerteEncours() {
        return getEntity().getSeuilAlerteEncours();
    }
}
