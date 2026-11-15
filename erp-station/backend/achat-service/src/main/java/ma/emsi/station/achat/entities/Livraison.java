package ma.emsi.station.achat.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Reception d'un camion-citerne. La cuve est une reference logique vers stock-service. */
@Entity
@Table(name = "livraison")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_livraison", nullable = false)
    private LocalDateTime dateLivraison;

    @Column(name = "numero_bon")
    private String numeroBon;

    @Column(nullable = false)
    private Double quantite;

    /** Prix d'achat en DH HT par litre. */
    @Column(name = "prix_achat_unitaire", nullable = false, precision = 10, scale = 3)
    private BigDecimal prixAchatUnitaire;

    @Column(name = "cuve_id", nullable = false)
    private Long cuveId;

    /** Copies (denormalisees) pour l'affichage sans appeler stock-service. */
    @Column(name = "cuve_code")
    private String cuveCode;

    @Column(name = "carburant_id", nullable = false)
    private Long carburantId;

    @Column(name = "carburant_nom")
    private String carburantNom;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fournisseur_id")
    private Fournisseur fournisseur;
}
