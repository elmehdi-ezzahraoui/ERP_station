package ma.emsi.station.achat.repositories;

import ma.emsi.station.achat.entities.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    List<Livraison> findByDateLivraisonBetweenOrderByDateLivraisonDesc(LocalDateTime debut, LocalDateTime fin);

    List<Livraison> findByFournisseurIdOrderByDateLivraisonDesc(Long fournisseurId);
}
