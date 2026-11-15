package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Releve du compteur (index) d'un pistolet pendant un poste :
 * index de debut a l'ouverture, index de fin a la cloture. Volume vendu = fin - debut.
 */
@Entity
@Table(name = "releve_index", uniqueConstraints = @UniqueConstraint(columnNames = {"poste_id", "pistolet_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReleveIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "poste_id")
    private PosteTravail poste;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pistolet_id")
    private Pistolet pistolet;

    /** Index de fin du poste precedent sur ce pistolet. */
    @Column(name = "dernier_index_connu", nullable = false)
    private Double dernierIndexConnu;

    @Column(name = "index_debut", nullable = false)
    private Double indexDebut;

    @Column(name = "index_fin")
    private Double indexFin;

    @Column(name = "volume_compteur")
    private Double volumeCompteur;

    @Column(name = "prix_unitaire", precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_compteur", precision = 12, scale = 2)
    private BigDecimal montantCompteur;

    /** true si l'index de debut ne correspond pas au dernier index connu (oubli, erreur ou fraude). */
    @Column(name = "index_discontinu", nullable = false)
    private boolean indexDiscontinu;
}
