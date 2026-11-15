package ma.emsi.station.stock.repositories;

import ma.emsi.station.stock.entities.Cuve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuveRepository extends JpaRepository<Cuve, Long> {

    boolean existsByCode(String code);

    @Query("select c from Cuve c where c.volumeActuel <= c.seuilAlerte")
    List<Cuve> findEnAlerte();

}
