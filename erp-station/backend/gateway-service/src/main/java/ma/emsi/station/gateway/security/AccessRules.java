package ma.emsi.station.gateway.security;

import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Set;

/**
 * Matrice d'autorisation par role (RBAC) appliquee par la Gateway.
 * La premiere regle qui correspond (chemin + methode) s'applique. ADMIN a tous les droits.
 * Un chemin sans regle est refuse (sauf pour ADMIN).
 */
public final class AccessRules {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final String POMPISTE = "POMPISTE";
    private static final String GERANT = "GERANT";
    private static final String COMPTABLE = "COMPTABLE";
    private static final Set<String> TOUS = Set.of(POMPISTE, GERANT, COMPTABLE);

    private static final Set<String> PUBLIC_PATHS = Set.of("/api/auth/login", "/actuator/health");

    /** Endpoints reserves a la communication inter-services (OpenFeign), jamais exposes. */
    private static final List<Rule> INTERNAL = List.of(
            new Rule("/api/cuves/*/sortie", HttpMethod.POST, Set.of()),
            new Rule("/api/cuves/*/entree", HttpMethod.POST, Set.of()),
            new Rule("/api/clients/*/consommations", HttpMethod.POST, Set.of())
    );

    private static final List<Rule> RULES = List.of(
            // Authentification : chaque utilisateur connecte peut voir son profil et changer son mot de passe
            new Rule("/api/auth/**", null, TOUS),
            // /api/utilisateurs/** : aucune regle -> ADMIN uniquement
            // Ventes & Caisse
            new Rule("/api/ventes/**", HttpMethod.POST, Set.of(POMPISTE, GERANT)),
            new Rule("/api/ventes/**", HttpMethod.GET, TOUS),
            new Rule("/api/postes/**", null, Set.of(POMPISTE, GERANT)),
            new Rule("/api/clotures/**", null, Set.of(POMPISTE, GERANT)),
            new Rule("/api/pompes/**", HttpMethod.GET, Set.of(POMPISTE, GERANT)),
            new Rule("/api/pistolets/**", HttpMethod.GET, Set.of(POMPISTE, GERANT)),
            new Rule("/api/pompes/**", null, Set.of(GERANT)),
            new Rule("/api/pistolets/**", null, Set.of(GERANT)),
            // Stock
            new Rule("/api/carburants/**", HttpMethod.GET, TOUS),
            new Rule("/api/cuves/**", HttpMethod.GET, Set.of(POMPISTE, GERANT)),
            new Rule("/api/carburants/**", null, Set.of(GERANT)),
            new Rule("/api/cuves/**", null, Set.of(GERANT)),
            new Rule("/api/jaugeages/**", null, Set.of(GERANT)),
            // Achat
            new Rule("/api/fournisseurs/**", null, Set.of(GERANT)),
            new Rule("/api/livraisons/**", HttpMethod.GET, Set.of(GERANT, COMPTABLE)),
            new Rule("/api/livraisons/**", null, Set.of(GERANT)),
            // Clients & Facturation (le pompiste peut consulter un client pour une vente a credit)
            new Rule("/api/clients/**", HttpMethod.GET, TOUS),
            new Rule("/api/clients/**", null, Set.of(COMPTABLE)),
            new Rule("/api/factures/**", null, Set.of(COMPTABLE)),
            new Rule("/api/reglements/**", null, Set.of(COMPTABLE)),
            // Parametrage : gerant et comptable consultent, chacun modifie le sien
            new Rule("/api/parametres/**", HttpMethod.GET, Set.of(GERANT, COMPTABLE)),
            new Rule("/api/parametres/financiers/**", null, Set.of(COMPTABLE)),
            new Rule("/api/parametres/controle/**", null, Set.of(GERANT)),
            // Statistiques & Pilotage
            new Rule("/api/statistiques/**", HttpMethod.GET, Set.of(GERANT, COMPTABLE))
    );

    private AccessRules() {
    }

    public static boolean isPublic(String path) {
        return PUBLIC_PATHS.stream().anyMatch(p -> MATCHER.match(p, path));
    }

    public static boolean isInternal(HttpMethod method, String path) {
        return INTERNAL.stream().anyMatch(r -> r.matches(method, path));
    }

    public static boolean isAllowed(String role, HttpMethod method, String path) {
        if ("ADMIN".equals(role)) {
            return true;
        }
        return RULES.stream()
                .filter(r -> r.matches(method, path))
                .findFirst()
                .map(r -> r.roles().contains(role))
                .orElse(false);
    }

    private record Rule(String pattern, HttpMethod method, Set<String> roles) {
        boolean matches(HttpMethod m, String path) {
            return (method == null || method.equals(m)) && MATCHER.match(pattern, path);
        }
    }
}
