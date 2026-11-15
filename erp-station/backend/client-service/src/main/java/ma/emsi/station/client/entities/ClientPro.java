package ma.emsi.station.client.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "client_pro")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientPro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "raison_sociale", nullable = false)
    private String raisonSociale;

    /** Identifiant Commun de l'Entreprise (15 chiffres). */
    @Column(unique = true, length = 15)
    private String ice;

    private String telephone;

    private String email;

    private String adresse;

    @Column(name = "plafond_credit", nullable = false, precision = 12, scale = 2)
    private BigDecimal plafondCredit;

    /** Encours : consommations non reglees (facturees ou non). */
    @Column(name = "solde_actuel", nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal soldeActuel = BigDecimal.ZERO;

    @Builder.Default
    private boolean actif = true;

    @Version
    private Long version;

    public BigDecimal getDisponible() {
        return plafondCredit.subtract(soldeActuel);
    }
}
