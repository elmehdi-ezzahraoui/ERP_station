package ma.emsi.station.vente.repositories;

import ma.emsi.station.vente.entities.ReleveIndex;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReleveIndexRepository extends JpaRepository<ReleveIndex, Long> {

    List<ReleveIndex> findByPosteIdOrderById(Long posteId);

    boolean existsByPosteIdAndPistoletId(Long posteId, Long pistoletId);

    /** Pistolet deja releve dans un poste encore ouvert. */
    boolean existsByPistoletIdAndIndexFinIsNull(Long pistoletId);
}
