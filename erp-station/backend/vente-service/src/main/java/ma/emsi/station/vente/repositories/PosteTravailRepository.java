package ma.emsi.station.vente.repositories;

import ma.emsi.station.vente.entities.PosteTravail;
import ma.emsi.station.vente.enums.StatutPoste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosteTravailRepository extends JpaRepository<PosteTravail, Long> {
    List<PosteTravail> findByStatut(StatutPoste statut);

    boolean existsByPompisteAndStatut(String pompiste, StatutPoste statut);
}
