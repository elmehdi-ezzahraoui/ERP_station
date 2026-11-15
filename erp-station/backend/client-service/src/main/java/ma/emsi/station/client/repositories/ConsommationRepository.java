package ma.emsi.station.client.repositories;

import ma.emsi.station.client.entities.Consommation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsommationRepository extends JpaRepository<Consommation, Long> {

    boolean existsByVenteId(Long venteId);

    List<Consommation> findByClientIdOrderByDateVenteDesc(Long clientId);

    List<Consommation> findByClientIdAndFactureIsNullAndDateVenteBetweenOrderByDateVente(
            Long clientId, LocalDateTime debut, LocalDateTime fin);
}
