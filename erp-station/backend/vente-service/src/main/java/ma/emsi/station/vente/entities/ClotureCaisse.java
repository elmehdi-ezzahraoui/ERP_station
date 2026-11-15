package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cloture_caisse")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClotureCaisse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_cloture", nullable = false)
    private LocalDateTime dateCloture;

    /** Volume total vendu d'apres les compteurs (somme des index fin - debut). */
    @Column(name = "volume_compteurs")
    private Double volumeCompteurs;

    /** Valeur des volumes compteurs au prix de vente. */
    @Column(name = "montant_compteurs", precision = 12, scale = 2)
    private BigDecimal montantCompteurs;

    @Column(name = "total_especes", precision = 12, scale = 2)
    private BigDecimal totalEspeces;

    @Column(name = "total_carte", precision = 12, scale = 2)
    private BigDecimal totalCarte;

    @Column(name = "total_cheque", precision = 12, scale = 2)
    private BigDecimal totalCheque;

    @Column(name = "total_credit", precision = 12, scale = 2)
    private BigDecimal totalCredit;

    /** Especes que le pompiste doit remettre : montant compteurs - carte - cheque - credit. */
    @Column(name = "especes_attendues", precision = 12, scale = 2)
    private BigDecimal especesAttendues;

    /** Fond de caisse + especes attendues. */
    @Column(name = "total_theorique", precision = 12, scale = 2)
    private BigDecimal totalTheorique;

    /** Argent reellement compte dans la caisse. */
    @Column(name = "total_physique", precision = 12, scale = 2)
    private BigDecimal totalPhysique;

    /** totalPhysique - totalTheorique (negatif = manquant). */
    @Column(precision = 12, scale = 2)
    private BigDecimal ecart;

    @Column(name = "seuil_ecart_applique", precision = 10, scale = 2)
    private BigDecimal seuilEcartApplique;

    @Column(name = "ecart_hors_tolerance", nullable = false)
    private boolean ecartHorsTolerance;

    @Column(name = "nb_index_discontinus", nullable = false)
    private int nbIndexDiscontinus;

    @OneToOne(optional = false)
    @JoinColumn(name = "poste_id", unique = true)
    private PosteTravail poste;
}
