package ma.emsi.station.vente.repositories;

import ma.emsi.station.vente.dtos.ResumeCarburantDTO;
import ma.emsi.station.vente.entities.Vente;
import ma.emsi.station.vente.enums.ModePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VenteRepository extends JpaRepository<Vente, Long> {

    List<Vente> findByDateVenteBetweenOrderByDateVenteDesc(LocalDateTime debut, LocalDateTime fin);

    List<Vente> findByPosteId(Long posteId);

    @Query("""
            select sum(v.montantTotal) from Vente v
            where v.poste.id = :posteId and v.modePaiement = :mode
            """)
    BigDecimal totalParMode(@Param("posteId") Long posteId, @Param("mode") ModePaiement mode);

    @Query("""
            select sum(v.volume) from Vente v
            where v.poste.id = :posteId and v.pistolet.id = :pistoletId
            """)
    Double volumeSaisi(@Param("posteId") Long posteId, @Param("pistoletId") Long pistoletId);

    @Query("""
            select new ma.emsi.station.vente.dtos.ResumeCarburantDTO(
                v.carburantId, v.carburantNom, sum(v.volume), sum(v.montantTotal), count(v))
            from Vente v
            where v.dateVente >= :debut and v.dateVente < :fin
            group by v.carburantId, v.carburantNom
            """)
    List<ResumeCarburantDTO> resumeParCarburant(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
