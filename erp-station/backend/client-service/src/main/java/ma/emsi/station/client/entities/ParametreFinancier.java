package ma.emsi.station.client.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/** Parametrage financier de la station (une seule ligne, id = 1). */
@Entity
@Table(name = "parametre_financier")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParametreFinancier {

    public static final long ID_UNIQUE = 1L;

    @Id
    private Long id;

    /** Taux de TVA applique aux carburants (ex. 0.10 = 10 %). */
    @Column(name = "taux_tva", nullable = false, precision = 5, scale = 4)
    private BigDecimal tauxTva;

    /** Part du plafond (ex. 0.80 = 80 %) a partir de laquelle un client est signale. */
    @Column(name = "seuil_alerte_encours", nullable = false, precision = 5, scale = 4)
    private BigDecimal seuilAlerteEncours;
}
