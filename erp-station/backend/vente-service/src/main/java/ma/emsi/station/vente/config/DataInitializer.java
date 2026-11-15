package ma.emsi.station.vente.config;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.vente.entities.Pistolet;
import ma.emsi.station.vente.entities.Pompe;
import ma.emsi.station.vente.repositories.PompeRepository;
import ma.emsi.station.vente.services.ParametreControleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Pompes de demonstration. Les cuveId 1 (Gasoil) et 2 (Sans Plomb) correspondent
 * aux cuves creees par le DataInitializer de stock-service.
 * Pistolets : 1 = pompe 1 gasoil, 2 = pompe 1 sans plomb, 3 = pompe 2 gasoil, 4 = pompe 2 sans plomb.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PompeRepository pompeRepository;
    private final ParametreControleService parametreService;

    @Override
    public void run(String... args) {
        parametreService.getEntity(); // ecart tolere 20 DH, controle de continuite actif
        if (pompeRepository.count() > 0) {
            return;
        }
        double index = 10000.0;
        for (int n = 1; n <= 2; n++) {
            Pompe pompe = Pompe.builder().numero(n).build();
            pompe.getPistolets().add(Pistolet.builder().numero(1).cuveId(1L).dernierIndex(index).pompe(pompe).build());
            pompe.getPistolets().add(Pistolet.builder().numero(2).cuveId(2L).dernierIndex(index + 10000).pompe(pompe).build());
            pompeRepository.save(pompe);
            index += 20000;
        }
    }
}
