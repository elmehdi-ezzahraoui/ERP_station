package ma.emsi.station.vente.repositories;

import ma.emsi.station.vente.entities.ClotureCaisse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClotureCaisseRepository extends JpaRepository<ClotureCaisse, Long> {
    List<ClotureCaisse> findByDateClotureBetweenOrderByDateClotureDesc(LocalDateTime debut, LocalDateTime fin);
}
