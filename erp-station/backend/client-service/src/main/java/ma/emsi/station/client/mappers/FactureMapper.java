package ma.emsi.station.client.mappers;

import ma.emsi.station.client.dtos.*;
import ma.emsi.station.client.entities.*;

/**
 * Conversion Facture (entite) vers FactureDTO.
 */
public final class FactureMapper {

    private FactureMapper() {
    }

    public static FactureDTO toDto(Facture f) {
        ClientPro c = f.getClient();
        return new FactureDTO(f.getId(), f.getNumeroFacture(), f.getDateFacture(), f.getPeriodeDebut(),
                f.getPeriodeFin(), c.getId(), c.getRaisonSociale(), c.getIce(), f.getTauxTva(),
                f.getMontantHT(), f.getMontantTVA(), f.getMontantTTC(), f.getMontantRegle(), f.getResteAPayer(),
                f.getStatut().name(),
                f.getLignes().stream().map(l -> new LigneFactureDTO(l.getDesignation(), l.getQuantite(),
                        l.getPrixUnitaireHT(), l.getMontantHT(), l.getMontantTTC())).toList());
    }
}
