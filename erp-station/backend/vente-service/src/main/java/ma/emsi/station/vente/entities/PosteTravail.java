package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.emsi.station.vente.enums.StatutPoste;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Prise de service d'un pompiste (de l'ouverture a la cloture de sa caisse). */
@Entity
@Table(name = "poste_travail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PosteTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pompiste;

    @Column(name = "date_ouverture", nullable = false)
    private LocalDateTime dateOuverture;

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    /** Fond de caisse remis en debut de service. */
    @Column(name = "fond_caisse", nullable = false, precision = 12, scale = 2)
    private BigDecimal fondCaisse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPoste statut;
}
