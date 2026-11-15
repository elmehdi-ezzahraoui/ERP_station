package ma.emsi.station.client.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ligne_facture")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String designation;

    /** Quantite en litres. */
    @Column(nullable = false)
    private Double quantite;

    @Column(name = "prix_unitaire_ht", nullable = false, precision = 10, scale = 3)
    private BigDecimal prixUnitaireHT;

    @Column(name = "montant_ht", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantHT;

    @Column(name = "montant_ttc", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTTC;

    @ManyToOne(optional = false)
    @JoinColumn(name = "facture_id")
    private Facture facture;
}
