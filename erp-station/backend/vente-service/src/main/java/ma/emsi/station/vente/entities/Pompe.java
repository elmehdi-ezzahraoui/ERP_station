package ma.emsi.station.vente.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pompe")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pompe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Builder.Default
    private boolean active = true;

    @Builder.Default
    @OneToMany(mappedBy = "pompe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pistolet> pistolets = new ArrayList<>();
}
