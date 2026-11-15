package ma.emsi.station.achat.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "stock-service")
public interface StockClient {

    /** Vue partielle de la cuve renvoyee par stock-service. */
    record CuveInfo(Long id, String code, Double capaciteMax, Double volumeActuel, Long carburantId, String carburantNom) {
    }

    record EntreeStockRequest(Double quantite, String reference) {
    }

    @GetMapping("/api/cuves/{id}")
    CuveInfo getCuve(@PathVariable("id") Long id);

    @PostMapping("/api/cuves/{id}/entree")
    CuveInfo entree(@PathVariable("id") Long id, @RequestBody EntreeStockRequest request);
}
