package ma.emsi.station.authentification.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.emsi.station.authentification.enums.Role;

@Entity
@Table(name = "utilisateur")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    /** Mot de passe hache avec BCrypt (jamais stocke en clair). */
    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(name = "nom_complet")
    private String nomComplet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private boolean actif = true;
}
