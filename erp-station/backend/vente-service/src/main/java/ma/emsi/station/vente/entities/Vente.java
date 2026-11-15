package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.emsi.station.vente.enums.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vente", indexes = @Index(name = "idx_vente_date", columnList = "date_vente"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_vente", nullable = false)
    private LocalDateTime dateVente;

    /** Volume en litres. */
    @Column(nullable = false)
    private Double volume;

    /** Prix TTC / litre au moment de la vente (historise). */
    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    /** References logiques vers stock-service et client-service. */
    @Column(name = "cuve_id", nullable = false)
    private Long cuveId;

    @Column(name = "carburant_id", nullable = false)
    private Long carburantId;

    /** Nom du carburant copie (denormalise) pour eviter un appel REST a chaque lecture. */
    @Column(name = "carburant_nom", nullable = false)
    private String carburantNom;

    @Column(name = "client_id")
    private Long clientId;

    /** true si la vente a ete deduite des index a la cloture (ventes especes non saisies une par une). */
    @Column(name = "calculee_par_index", nullable = false)
    @Builder.Default
    private boolean calculeeParIndex = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pistolet_id")
    private Pistolet pistolet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "poste_id")
    private PosteTravail poste;
}
