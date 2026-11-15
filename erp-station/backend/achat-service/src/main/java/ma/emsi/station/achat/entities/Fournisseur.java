package ma.emsi.station.achat.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fournisseur")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Fournisseur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String telephone;

    private String email;

    private String adresse;
}
