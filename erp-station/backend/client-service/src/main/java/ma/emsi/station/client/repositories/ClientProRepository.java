package ma.emsi.station.client.repositories;

import ma.emsi.station.client.entities.ClientPro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientProRepository extends JpaRepository<ClientPro, Long> {
    boolean existsByIce(String ice);
}
