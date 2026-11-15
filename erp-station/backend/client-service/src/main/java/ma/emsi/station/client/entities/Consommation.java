package ma.emsi.station.client.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Vente a credit (bon) inscrite sur le compte d'un client, en attente de facturation. */
@Entity
@Table(name = "consommation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Consommation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference logique de la vente dans vente-service (unique : idempotence). */
    @Column(name = "vente_id", nullable = false, unique = true)
    private Long venteId;

    @Column(name = "date_vente", nullable = false)
    private LocalDateTime dateVente;

    @Column(nullable = false)
    private String carburant;

    @Column(nullable = false)
    private Double volume;

    @Column(name = "prix_unitaire", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "montant_ttc", nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTTC;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private ClientPro client;

    @ManyToOne
    @JoinColumn(name = "facture_id")
    private Facture facture;

    public boolean isFacturee() {
        return facture != null;
    }
}
