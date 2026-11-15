package ma.emsi.station.client.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reglement")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Reglement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_reglement", nullable = false)
    private LocalDateTime dateReglement;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    /** ESPECES, CHEQUE, VIREMENT, EFFET... */
    @Column(name = "mode_reglement", nullable = false)
    private String modeReglement;

    private String reference;

    @ManyToOne(optional = false)
    @JoinColumn(name = "facture_id")
    private Facture facture;
}
