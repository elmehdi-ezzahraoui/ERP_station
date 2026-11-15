package ma.emsi.station.stock.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuve")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cuve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    /** Capacite maximale en litres. */
    @Column(name = "capacite_max", nullable = false)
    private Double capaciteMax;

    /** Stock theorique actuel en litres. */
    @Column(name = "volume_actuel", nullable = false)
    private Double volumeActuel;

    /** Niveau minimal declenchant l'alerte de reachat. */
    @Column(name = "seuil_alerte", nullable = false)
    private Double seuilAlerte;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "carburant_id")
    private Carburant carburant;

    /** Verrouillage optimiste : evite deux ventes simultanees qui ecrasent le meme stock. */
    @Version
    private Long version;

    public boolean isEnAlerte() {
        return volumeActuel <= seuilAlerte;
    }
}
