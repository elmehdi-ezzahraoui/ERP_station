package ma.emsi.station.vente.repositories;

import ma.emsi.station.vente.entities.Pompe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PompeRepository extends JpaRepository<Pompe, Long> {
    boolean existsByNumero(Integer numero);
}
