package ma.emsi.station.vente.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Index de debut de poste, un par pistolet utilise par le pompiste. */
public record ReleveIndexRequest(@NotEmpty List<@Valid IndexPistoletRequest> releves) {
}
