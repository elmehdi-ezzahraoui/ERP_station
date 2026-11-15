package ma.emsi.station.achat.mappers;

import ma.emsi.station.achat.dtos.FournisseurDTO;
import ma.emsi.station.achat.dtos.LivraisonDTO;
import ma.emsi.station.achat.entities.Fournisseur;
import ma.emsi.station.achat.entities.Livraison;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Conversion Fournisseur / Livraison (entites) vers les DTO.
 */
public final class AchatMapper {

    private AchatMapper() {
    }

    public static FournisseurDTO toDto(Fournisseur f) {
        return new FournisseurDTO(f.getId(), f.getNom(), f.getTelephone(), f.getEmail(), f.getAdresse());
    }

    public static LivraisonDTO toDto(Livraison l) {
        BigDecimal total = l.getPrixAchatUnitaire().multiply(BigDecimal.valueOf(l.getQuantite()))
                .setScale(2, RoundingMode.HALF_UP);
        return new LivraisonDTO(l.getId(), l.getDateLivraison(), l.getNumeroBon(), l.getQuantite(),
                l.getPrixAchatUnitaire(), total, l.getCuveId(), l.getCuveCode(), l.getCarburantNom(),
                l.getFournisseur().getId(), l.getFournisseur().getNom());
    }
}
