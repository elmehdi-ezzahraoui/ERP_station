package ma.emsi.station.vente.dtos;

import java.math.BigDecimal;

public record ReleveIndexDTO(Long id, Long pistoletId, Integer pompeNumero, Integer pistoletNumero, Long cuveId,
                             Double dernierIndexConnu, Double indexDebut, Double indexFin, Double volumeCompteur,
                             BigDecimal prixUnitaire, BigDecimal montantCompteur, boolean indexDiscontinu) {
}
