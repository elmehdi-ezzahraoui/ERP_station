package ma.emsi.station.achat.config;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.achat.entities.Fournisseur;
import ma.emsi.station.achat.entities.Livraison;
import ma.emsi.station.achat.repositories.FournisseurRepository;
import ma.emsi.station.achat.repositories.LivraisonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Fournisseur et historique de livraisons de demonstration (cuves 1 et 2 de stock-service).
 * Ces livraisons sont deja comptees dans le stock initial : on n'appelle pas stock-service ici.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final FournisseurRepository fournisseurRepository;
    private final LivraisonRepository livraisonRepository;

    @Override
    public void run(String... args) {
        if (fournisseurRepository.count() > 0) {
            return;
        }
        Fournisseur f = fournisseurRepository.save(Fournisseur.builder()
                .nom("Depot Petrolier Mohammedia").telephone("0523000000").email("depot@exemple.ma")
                .adresse("Mohammedia").build());
        livraisonRepository.save(Livraison.builder().dateLivraison(LocalDateTime.now().minusDays(3))
                .numeroBon("BL-0001").quantite(15000.0).prixAchatUnitaire(new BigDecimal("9.150"))
                .cuveId(1L).cuveCode("CUVE-GO-01").carburantId(1L).carburantNom("Gasoil")
                .fournisseur(f).build());
        livraisonRepository.save(Livraison.builder().dateLivraison(LocalDateTime.now().minusDays(5))
                .numeroBon("BL-0002").quantite(10000.0).prixAchatUnitaire(new BigDecimal("10.700"))
                .cuveId(2L).cuveCode("CUVE-SP-01").carburantId(2L).carburantNom("Essence Sans Plomb")
                .fournisseur(f).build());
    }
}
