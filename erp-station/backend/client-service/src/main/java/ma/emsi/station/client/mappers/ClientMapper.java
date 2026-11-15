package ma.emsi.station.client.mappers;

import ma.emsi.station.client.dtos.*;
import ma.emsi.station.client.entities.ClientPro;
import ma.emsi.station.client.entities.Consommation;

/**
 * Conversion ClientPro / Consommation (entites) vers les DTO.
 */
public final class ClientMapper {

    private ClientMapper() {
    }

    public static ClientDTO toDto(ClientPro c) {
        return new ClientDTO(c.getId(), c.getRaisonSociale(), c.getIce(), c.getTelephone(), c.getEmail(),
                c.getAdresse(), c.getPlafondCredit(), c.getSoldeActuel(), c.getDisponible(), c.isActif());
    }

    public static ConsommationDTO toDto(Consommation c) {
        return new ConsommationDTO(c.getId(), c.getVenteId(), c.getDateVente(), c.getCarburant(), c.getVolume(),
                c.getPrixUnitaire(), c.getMontantTTC(),
                c.isFacturee() ? c.getFacture().getNumeroFacture() : null);
    }
}
