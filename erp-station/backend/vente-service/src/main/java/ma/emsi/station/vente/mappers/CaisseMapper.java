package ma.emsi.station.vente.mappers;

import ma.emsi.station.vente.dtos.ClotureDTO;
import ma.emsi.station.vente.dtos.PosteDTO;
import ma.emsi.station.vente.dtos.ReleveIndexDTO;
import ma.emsi.station.vente.entities.ClotureCaisse;
import ma.emsi.station.vente.entities.PosteTravail;
import ma.emsi.station.vente.entities.ReleveIndex;

import java.util.List;

/**
 * Conversion PosteTravail / ReleveIndex / ClotureCaisse (entites) vers les DTO.
 */
public final class CaisseMapper {

    private CaisseMapper() {
    }

    public static PosteDTO toDto(PosteTravail p) {
        return new PosteDTO(p.getId(), p.getPompiste(), p.getDateOuverture(), p.getDateCloture(),
                p.getFondCaisse(), p.getStatut().name());
    }

    public static ReleveIndexDTO toDto(ReleveIndex r) {
        return new ReleveIndexDTO(r.getId(), r.getPistolet().getId(), r.getPistolet().getPompe().getNumero(),
                r.getPistolet().getNumero(), r.getPistolet().getCuveId(), r.getDernierIndexConnu(),
                r.getIndexDebut(), r.getIndexFin(), r.getVolumeCompteur(), r.getPrixUnitaire(),
                r.getMontantCompteur(), r.isIndexDiscontinu());
    }

    public static ClotureDTO toDto(ClotureCaisse c, List<ReleveIndex> releves) {
        PosteTravail p = c.getPoste();
        return new ClotureDTO(c.getId(), p.getId(), p.getPompiste(), c.getDateCloture(), p.getFondCaisse(),
                c.getVolumeCompteurs(), c.getMontantCompteurs(),
                c.getTotalEspeces(), c.getTotalCarte(), c.getTotalCheque(), c.getTotalCredit(),
                c.getEspecesAttendues(), c.getTotalTheorique(), c.getTotalPhysique(),
                c.getEcart(), c.getSeuilEcartApplique(), c.isEcartHorsTolerance(),
                c.getNbIndexDiscontinus(), releves.stream().map(CaisseMapper::toDto).toList());
    }
}
