package ma.emsi.station.stock.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "carburant")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Carburant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private Double densite;

    /** Prix de vente a la pompe en DH TTC par litre. */
    @Column(name = "prix_vente", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixVente;

    /**
     * Parametrage : ecart tolere entre stock theorique et jaugeage, en % du stock theorique
     * (evaporation, dilatation, precision de la jauge). Au-dela : anomalie de coulage.
     */
    @Column(name = "tolerance_coulage_pct", nullable = false)
    @Builder.Default
    private Double toleranceCoulagePct = 0.5;
}
