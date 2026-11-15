package ma.emsi.station.stock.config;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.stock.entities.Carburant;
import ma.emsi.station.stock.entities.Cuve;
import ma.emsi.station.stock.repositories.CarburantRepository;
import ma.emsi.station.stock.repositories.CuveRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Donnees de demonstration inserees au premier demarrage (prix et tolerances indicatifs, a ajuster par le gerant). */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CarburantRepository carburantRepository;
    private final CuveRepository cuveRepository;

    @Override
    public void run(String... args) {
        if (carburantRepository.count() > 0) {
            return;
        }
        Carburant gasoil = carburantRepository.save(Carburant.builder()
                .nom("Gasoil").densite(0.845).prixVente(new BigDecimal("11.20")).toleranceCoulagePct(0.3).build());
        Carburant essence = carburantRepository.save(Carburant.builder()
                .nom("Essence Sans Plomb").densite(0.745).prixVente(new BigDecimal("13.10")).toleranceCoulagePct(0.5).build());

        cuveRepository.save(Cuve.builder().code("CUVE-GO-01").capaciteMax(30000.0)
                .volumeActuel(18000.0).seuilAlerte(5000.0).carburant(gasoil).build());
        cuveRepository.save(Cuve.builder().code("CUVE-SP-01").capaciteMax(20000.0)
                .volumeActuel(4500.0).seuilAlerte(4000.0).carburant(essence).build());
    }
}
