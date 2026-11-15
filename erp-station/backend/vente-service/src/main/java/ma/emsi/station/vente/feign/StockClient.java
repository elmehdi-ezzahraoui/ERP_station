package ma.emsi.station.vente.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "stock-service")
public interface StockClient {

    record SortieStockRequest(Double quantite, String reference) {
    }

    @GetMapping("/api/cuves/{id}")
    CuveInfo getCuve(@PathVariable("id") Long id);

    @PostMapping("/api/cuves/{id}/sortie")
    CuveInfo sortie(@PathVariable("id") Long id, @RequestBody SortieStockRequest request);
}
