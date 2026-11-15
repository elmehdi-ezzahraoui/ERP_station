package ma.emsi.station.statistiques;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class StatistiquesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatistiquesServiceApplication.class, args);
    }
}
