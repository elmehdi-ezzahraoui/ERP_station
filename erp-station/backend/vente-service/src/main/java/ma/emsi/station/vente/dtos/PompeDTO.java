package ma.emsi.station.vente.dtos;

import java.util.List;

public record PompeDTO(Long id, Integer numero, boolean active, List<PistoletDTO> pistolets) {
}
