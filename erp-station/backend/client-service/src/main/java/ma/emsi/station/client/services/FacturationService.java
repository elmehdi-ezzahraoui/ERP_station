package ma.emsi.station.client.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.dtos.*;
import ma.emsi.station.client.entities.*;
import ma.emsi.station.client.enums.StatutFacture;
import ma.emsi.station.client.exceptions.BusinessException;
import ma.emsi.station.client.mappers.FactureMapper;
import ma.emsi.station.client.repositories.ConsommationRepository;
import ma.emsi.station.client.repositories.FactureRepository;
import ma.emsi.station.client.repositories.ReglementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FacturationService {

    private final FactureRepository factureRepository;
    private final ConsommationRepository consommationRepository;
    private final ReglementRepository reglementRepository;
    private final ClientService clientService;
    private final ParametreService parametreService;


    /**
     * Regroupe les consommations non facturees d'un client sur la periode (une ligne par carburant)
     * et emet la facture. Les prix pompe etant TTC, le HT est deduit : HT = TTC / (1 + TVA).
     */
    public FactureDTO generer(GenererFactureRequest r) {
        if (r.fin().isBefore(r.debut())) {
            throw BusinessException.badRequest("La date de fin precede la date de debut");
        }
        ClientPro client = clientService.getEntity(r.clientId());
        List<Consommation> consos = consommationRepository
                .findByClientIdAndFactureIsNullAndDateVenteBetweenOrderByDateVente(
                        client.getId(), r.debut().atStartOfDay(), r.fin().plusDays(1).atStartOfDay());
        if (consos.isEmpty()) {
            throw BusinessException.badRequest("Aucune consommation non facturee sur cette periode");
        }

        BigDecimal tauxTva = parametreService.getTauxTva();
        BigDecimal diviseur = BigDecimal.ONE.add(tauxTva);
        Facture facture = Facture.builder()
                .dateFacture(LocalDate.now())
                .periodeDebut(r.debut())
                .periodeFin(r.fin())
                .tauxTva(tauxTva)
                .statut(StatutFacture.EMISE)
                .client(client)
                .montantHT(BigDecimal.ZERO).montantTVA(BigDecimal.ZERO).montantTTC(BigDecimal.ZERO)
                .build();

        Map<String, List<Consommation>> parCarburant = consos.stream()
                .collect(Collectors.groupingBy(Consommation::getCarburant, LinkedHashMap::new, Collectors.toList()));

        BigDecimal totalTTC = BigDecimal.ZERO;
        BigDecimal totalHT = BigDecimal.ZERO;
        for (Map.Entry<String, List<Consommation>> e : parCarburant.entrySet()) {
            double qte = e.getValue().stream().mapToDouble(Consommation::getVolume).sum();
            BigDecimal ttc = e.getValue().stream().map(Consommation::getMontantTTC)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal ht = ttc.divide(diviseur, 2, RoundingMode.HALF_UP);
            BigDecimal puHT = ht.divide(BigDecimal.valueOf(qte), 3, RoundingMode.HALF_UP);
            facture.getLignes().add(LigneFacture.builder()
                    .designation(e.getKey() + " (" + e.getValue().size() + " bons)")
                    .quantite(Math.round(qte * 1000) / 1000.0)
                    .prixUnitaireHT(puHT)
                    .montantHT(ht)
                    .montantTTC(ttc)
                    .facture(facture)
                    .build());
            totalTTC = totalTTC.add(ttc);
            totalHT = totalHT.add(ht);
        }
        facture.setMontantTTC(totalTTC);
        facture.setMontantHT(totalHT);
        facture.setMontantTVA(totalTTC.subtract(totalHT));

        Facture saved = factureRepository.save(facture);
        saved.setNumeroFacture(String.format("FAC-%d-%05d", saved.getDateFacture().getYear(), saved.getId()));
        consos.forEach(c -> c.setFacture(saved));
        return FactureMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<FactureDTO> findAll(Long clientId) {
        List<Facture> list = clientId != null
                ? factureRepository.findByClientIdOrderByDateFactureDesc(clientId)
                : factureRepository.findAll();
        return list.stream().map(FactureMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public FactureDTO findById(Long id) {
        return FactureMapper.toDto(getFacture(id));
    }

    private Facture getFacture(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Facture introuvable : " + id));
    }

    /** Encaissement (total ou partiel) d'une facture : diminue l'encours du client. */
    public ReglementDTO regler(ReglementRequest r) {
        Facture f = getFacture(r.factureId());
        if (f.getStatut() == StatutFacture.PAYEE) {
            throw BusinessException.badRequest("La facture " + f.getNumeroFacture() + " est deja soldee");
        }
        if (r.montant().compareTo(f.getResteAPayer()) > 0) {
            throw BusinessException.badRequest("Montant superieur au reste a payer (" + f.getResteAPayer() + " DH)");
        }
        Reglement reg = reglementRepository.save(Reglement.builder()
                .dateReglement(LocalDateTime.now())
                .montant(r.montant())
                .modeReglement(r.modeReglement())
                .reference(r.reference())
                .facture(f)
                .build());
        f.getReglements().add(reg);
        f.setMontantRegle(f.getMontantRegle().add(r.montant()));
        f.setStatut(f.getResteAPayer().signum() == 0 ? StatutFacture.PAYEE : StatutFacture.PARTIELLEMENT_PAYEE);

        ClientPro c = f.getClient();
        c.setSoldeActuel(c.getSoldeActuel().subtract(r.montant()));

        return new ReglementDTO(reg.getId(), reg.getDateReglement(), reg.getMontant(), reg.getModeReglement(),
                reg.getReference(), f.getId(), f.getNumeroFacture(), f.getStatut().name());
    }
}
