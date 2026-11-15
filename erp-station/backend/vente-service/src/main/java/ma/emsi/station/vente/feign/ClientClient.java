package ma.emsi.station.vente.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@FeignClient(name = "client-service")
public interface ClientClient {

    record AutorisationCredit(Long clientId, String raisonSociale, boolean autorise, BigDecimal plafond,
                              BigDecimal encours, BigDecimal disponible, String motif) {
    }

    record ConsommationRequest(Long venteId, LocalDateTime dateVente, String carburant, Double volume,
                               BigDecimal prixUnitaire, BigDecimal montantTTC) {
    }

    @GetMapping("/api/clients/{id}/autorisation")
    AutorisationCredit verifierCredit(@PathVariable("id") Long clientId, @RequestParam("montant") BigDecimal montant);

    @PostMapping("/api/clients/{id}/consommations")
    void enregistrerConsommation(@PathVariable("id") Long clientId, @RequestBody ConsommationRequest request);
}
