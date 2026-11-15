package ma.emsi.station.client.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.emsi.station.client.enums.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facture")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_facture", unique = true)
    private String numeroFacture;

    @Column(name = "date_facture", nullable = false)
    private LocalDate dateFacture;

    @Column(name = "periode_debut", nullable = false)
    private LocalDate periodeDebut;

    @Column(name = "periode_fin", nullable = false)
    private LocalDate periodeFin;

    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 4)
    private BigDecimal tauxTva;

    @Column(name = "montant_ht", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantHT;

    @Column(name = "montant_tva", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTVA;

    @Column(name = "montant_ttc", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTTC;

    @Column(name = "montant_regle", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal montantRegle = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientPro client;

    @Builder.Default
    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneFacture> lignes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "facture")
    private List<Reglement> reglements = new ArrayList<>();

    public BigDecimal getResteAPayer() {
        return montantTTC.subtract(montantRegle);
    }
}
