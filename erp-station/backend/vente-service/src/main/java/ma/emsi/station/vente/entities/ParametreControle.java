package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Parametrage du controle de la caisse (une seule ligne, id = 1). */
@Entity
@Table(name = "parametre_controle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParametreControle {

    public static final long ID_UNIQUE = 1L;

    @Id
    private Long id;

    /** Ecart de caisse tolere (en DH, en plus ou en moins) avant de signaler une anomalie. */
    @Column(name = "seuil_ecart_caisse", nullable = false, precision = 10, scale = 2)
    private BigDecimal seuilEcartCaisse;

    /** Verifier que l'index de debut d'un poste = index de fin du poste precedent. */
    @Column(name = "controle_continuite_index", nullable = false)
    private boolean controleContinuiteIndex;
}
