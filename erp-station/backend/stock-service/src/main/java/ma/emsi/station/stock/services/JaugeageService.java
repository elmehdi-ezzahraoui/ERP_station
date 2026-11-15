package ma.emsi.station.stock.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.emsi.station.stock.dtos.JaugeageDTO;
import ma.emsi.station.stock.dtos.JaugeageRequest;
import ma.emsi.station.stock.entities.Cuve;
import ma.emsi.station.stock.entities.Jaugeage;
import ma.emsi.station.stock.mappers.StockMapper;
import ma.emsi.station.stock.repositories.JaugeageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Jaugeage : compare le stock theorique (livraisons - ventes) au volume mesure dans la cuve.
 * Un ecart superieur a la tolerance du carburant est signale comme anomalie de coulage.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class JaugeageService {

    private final JaugeageRepository jaugeageRepository;
    private final CuveService cuveService;

    @Transactional(readOnly = true)
    public List<JaugeageDTO> findByCuve(Long cuveId) {
        return jaugeageRepository.findByCuveIdOrderByDateReleveDesc(cuveId).stream().map(StockMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<JaugeageDTO> findBetween(LocalDate debut, LocalDate fin) {
        return jaugeageRepository
                .findByDateReleveBetweenOrderByDateReleveDesc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(StockMapper::toDto).toList();
    }

    public JaugeageDTO jauger(JaugeageRequest r) {
        Cuve cuve = cuveService.getEntity(r.cuveId());
        double theorique = cuve.getVolumeActuel();
        double ecart = Math.round((r.volumeMesure() - theorique) * 1000) / 1000.0;
        double tolerance = cuve.getCarburant().getToleranceCoulagePct();
        double ecartPct = theorique == 0 ? 0 : Math.abs(ecart) * 100 / theorique;
        boolean anomalie = ecartPct > tolerance;
        if (anomalie) {
            log.warn("ANOMALIE DE COULAGE : cuve {} ecart {} L ({} %, tolerance {} %)",
                    cuve.getCode(), ecart, Math.round(ecartPct * 100) / 100.0, tolerance);
        }
        Jaugeage j = Jaugeage.builder()
                .dateReleve(LocalDateTime.now())
                .volumeTheorique(theorique)
                .volumeMesure(r.volumeMesure())
                .ecart(ecart)
                .toleranceAppliquee(tolerance)
                .anomalie(anomalie)
                .cuve(cuve)
                .build();
        if (r.ajusterStock()) {
            cuve.setVolumeActuel(r.volumeMesure());
        }
        return StockMapper.toDto(jaugeageRepository.save(j));
    }
}
