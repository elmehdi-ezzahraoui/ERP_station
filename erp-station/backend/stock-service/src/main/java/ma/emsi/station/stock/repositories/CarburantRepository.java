package ma.emsi.station.stock.repositories;

import ma.emsi.station.stock.entities.Carburant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarburantRepository extends JpaRepository<Carburant, Long> {

    Optional<Carburant> findByNomIgnoreCase(String nom);

}
