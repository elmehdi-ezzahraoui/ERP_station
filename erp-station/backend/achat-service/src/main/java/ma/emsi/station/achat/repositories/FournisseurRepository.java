package ma.emsi.station.achat.repositories;

import ma.emsi.station.achat.entities.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
}
