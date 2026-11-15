package ma.emsi.station.client.config;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.entities.ClientPro;
import ma.emsi.station.client.repositories.ClientProRepository;
import ma.emsi.station.client.services.ParametreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Clients professionnels fictifs de demonstration. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClientProRepository clientRepository;
    private final ParametreService parametreService;

    @Override
    public void run(String... args) {
        parametreService.getEntity(); // TVA 10 % et seuil 80 % par defaut
        if (clientRepository.count() > 0) {
            return;
        }
        clientRepository.save(ClientPro.builder().raisonSociale("Transport Atlas SARL")
                .ice("001234567000089").telephone("0522000001").adresse("Casablanca")
                .plafondCredit(new BigDecimal("20000.00")).build());
        clientRepository.save(ClientPro.builder().raisonSociale("Taxi Express Mohammedia")
                .ice("002345678000012").telephone("0523000002").adresse("Mohammedia")
                .plafondCredit(new BigDecimal("1000.00")).build());
    }
}
