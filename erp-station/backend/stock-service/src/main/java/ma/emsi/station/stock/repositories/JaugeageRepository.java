package ma.emsi.station.stock.repositories;

import ma.emsi.station.stock.entities.Jaugeage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface JaugeageRepository extends JpaRepository<Jaugeage, Long> {

    List<Jaugeage> findByCuveIdOrderByDateReleveDesc(Long cuveId);

    List<Jaugeage> findByDateReleveBetweenOrderByDateReleveDesc(LocalDateTime debut, LocalDateTime fin);
}
