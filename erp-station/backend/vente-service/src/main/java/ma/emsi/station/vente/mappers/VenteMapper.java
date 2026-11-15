package ma.emsi.station.vente.mappers;

import ma.emsi.station.vente.dtos.*;
import ma.emsi.station.vente.entities.*;

/**
 * Conversion Vente (entite) vers VenteDTO.
 */
public final class VenteMapper {

    private VenteMapper() {
    }

    public static VenteDTO toDto(Vente v) {
        return new VenteDTO(v.getId(), v.getDateVente(), v.getPistolet().getPompe().getNumero(),
                v.getPistolet().getNumero(), v.getCarburantNom(), v.getVolume(), v.getPrixUnitaire(),
                v.getMontantTotal(), v.getModePaiement(), v.getClientId(), v.getPoste().getId(),
                v.getPoste().getPompiste());
    }
}
