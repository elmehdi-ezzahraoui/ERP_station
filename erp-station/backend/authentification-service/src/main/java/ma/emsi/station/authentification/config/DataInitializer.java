package ma.emsi.station.authentification.config;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.authentification.entities.Utilisateur;
import ma.emsi.station.authentification.enums.Role;
import ma.emsi.station.authentification.repositories.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Comptes de demonstration crees au premier demarrage. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (utilisateurRepository.count() > 0) {
            return;
        }
        creer("admin", "admin123", "Administrateur", Role.ADMIN);
        creer("gerant", "gerant123", "Gerant de la station", Role.GERANT);
        creer("pompiste1", "pompiste123", "Pompiste 1", Role.POMPISTE);
        creer("comptable", "comptable123", "Comptable", Role.COMPTABLE);
    }

    private void creer(String username, String motDePasse, String nom, Role role) {
        utilisateurRepository.save(Utilisateur.builder()
                .username(username)
                .motDePasse(passwordEncoder.encode(motDePasse))
                .nomComplet(nom)
                .role(role)
                .build());
    }
}
