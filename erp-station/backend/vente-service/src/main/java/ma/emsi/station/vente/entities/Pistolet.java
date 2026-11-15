package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pistolet")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pistolet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer numero;

    /** Reference logique vers la cuve (stock-service) : pas de cle etrangere inter-bases. */
    @Column(name = "cuve_id", nullable = false)
    private Long cuveId;

    /** Dernier index (totalisateur en litres) releve en fin de poste. Sert au controle de continuite. */
    @Column(name = "dernier_index", nullable = false)
    @Builder.Default
    private Double dernierIndex = 0.0;

    @Builder.Default
    private boolean active = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pompe_id")
    private Pompe pompe;
}
