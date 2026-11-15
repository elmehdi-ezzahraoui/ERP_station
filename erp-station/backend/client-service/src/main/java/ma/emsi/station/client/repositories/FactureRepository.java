package ma.emsi.station.client.repositories;

import ma.emsi.station.client.entities.Facture;
import ma.emsi.station.client.enums.StatutFacture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByClientIdOrderByDateFactureDesc(Long clientId);

    List<Facture> findByStatutNotOrderByDateFacture(StatutFacture statut);
}
