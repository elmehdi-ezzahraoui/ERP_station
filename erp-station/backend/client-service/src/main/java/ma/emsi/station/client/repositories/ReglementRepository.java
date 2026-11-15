package ma.emsi.station.client.repositories;

import ma.emsi.station.client.entities.Reglement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReglementRepository extends JpaRepository<Reglement, Long> {
}
