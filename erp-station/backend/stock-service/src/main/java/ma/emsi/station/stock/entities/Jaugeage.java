package ma.emsi.station.stock.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "jaugeage")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Jaugeage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_releve", nullable = false)
    private LocalDateTime dateReleve;

    @Column(name = "volume_theorique", nullable = false)
    private Double volumeTheorique;

    @Column(name = "volume_mesure", nullable = false)
    private Double volumeMesure;

    /** volumeMesure - volumeTheorique (negatif = perte). */
    @Column(nullable = false)
    private Double ecart;

    /** Tolerance (en %) appliquee au moment du releve. */
    @Column(name = "tolerance_appliquee", nullable = false)
    private Double toleranceAppliquee;

    /** true si l'ecart depasse la tolerance : coulage, fuite, vol ou erreur d'index. */
    @Column(nullable = false)
    private boolean anomalie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuve_id")
    private Cuve cuve;
}
