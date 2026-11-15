package ma.emsi.station.client.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.client.dtos.*;
import ma.emsi.station.client.entities.ClientPro;
import ma.emsi.station.client.entities.Consommation;
import ma.emsi.station.client.enums.StatutFacture;
import ma.emsi.station.client.exceptions.BusinessException;
import ma.emsi.station.client.mappers.ClientMapper;
import ma.emsi.station.client.repositories.ClientProRepository;
import ma.emsi.station.client.repositories.ConsommationRepository;
import ma.emsi.station.client.repositories.FactureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientProRepository clientRepository;
    private final ConsommationRepository consommationRepository;
    private final FactureRepository factureRepository;
    private final ParametreService parametreService;


    @Transactional(readOnly = true)
    public List<ClientDTO> findAll() {
        return clientRepository.findAll().stream().map(ClientMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ClientDTO findById(Long id) {
        return ClientMapper.toDto(getEntity(id));
    }

    public ClientPro getEntity(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Client introuvable : " + id));
    }

    public ClientDTO create(ClientRequest r) {
        if (r.ice() != null && clientRepository.existsByIce(r.ice())) {
            throw BusinessException.conflict("Un client avec l'ICE " + r.ice() + " existe deja");
        }
        ClientPro c = ClientPro.builder()
                .raisonSociale(r.raisonSociale()).ice(r.ice()).telephone(r.telephone())
                .email(r.email()).adresse(r.adresse()).plafondCredit(r.plafondCredit())
                .build();
        return ClientMapper.toDto(clientRepository.save(c));
    }

    public ClientDTO update(Long id, ClientRequest r) {
        ClientPro c = getEntity(id);
        c.setRaisonSociale(r.raisonSociale());
        c.setIce(r.ice());
        c.setTelephone(r.telephone());
        c.setEmail(r.email());
        c.setAdresse(r.adresse());
        c.setPlafondCredit(r.plafondCredit());
        if (r.actif() != null) {
            c.setActif(r.actif());
        }
        return ClientMapper.toDto(c);
    }

    /** Controle du plafond avant une vente a credit (appele par vente-service). */
    @Transactional(readOnly = true)
    public AutorisationCreditDTO verifierCredit(Long clientId, BigDecimal montant) {
        ClientPro c = getEntity(clientId);
        String motif = null;
        if (!c.isActif()) {
            motif = "compte client inactif";
        } else if (c.getSoldeActuel().add(montant).compareTo(c.getPlafondCredit()) > 0) {
            motif = "plafond de credit depasse (disponible : " + c.getDisponible() + " DH)";
        }
        return new AutorisationCreditDTO(c.getId(), c.getRaisonSociale(), motif == null,
                c.getPlafondCredit(), c.getSoldeActuel(), c.getDisponible(), motif);
    }

    /** Inscrit une vente a credit sur le compte et augmente l'encours (idempotent par venteId). */
    public ConsommationDTO enregistrerConsommation(Long clientId, ConsommationRequest r) {
        if (consommationRepository.existsByVenteId(r.venteId())) {
            throw BusinessException.conflict("La vente " + r.venteId() + " est deja inscrite");
        }
        AutorisationCreditDTO auto = verifierCredit(clientId, r.montantTTC());
        if (!auto.autorise()) {
            throw BusinessException.conflict("Credit refuse : " + auto.motif());
        }
        ClientPro c = getEntity(clientId);
        c.setSoldeActuel(c.getSoldeActuel().add(r.montantTTC()));
        Consommation conso = consommationRepository.save(Consommation.builder()
                .venteId(r.venteId()).dateVente(r.dateVente()).carburant(r.carburant())
                .volume(r.volume()).prixUnitaire(r.prixUnitaire()).montantTTC(r.montantTTC())
                .client(c)
                .build());
        return ClientMapper.toDto(conso);
    }

    @Transactional(readOnly = true)
    public List<ConsommationDTO> consommations(Long clientId) {
        return consommationRepository.findByClientIdOrderByDateVenteDesc(clientId).stream()
                .map(ClientMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public EncoursDTO encours() {
        List<ClientPro> clients = clientRepository.findAll();
        BigDecimal seuilAlerte = parametreService.getSeuilAlerteEncours();
        BigDecimal total = clients.stream().map(ClientPro::getSoldeActuel).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal impayes = factureRepository.findByStatutNotOrderByDateFacture(StatutFacture.PAYEE).stream()
                .map(f -> f.getResteAPayer()).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<ClientDTO> proches = clients.stream()
                .filter(c -> c.getPlafondCredit().signum() > 0
                        && c.getSoldeActuel().compareTo(c.getPlafondCredit().multiply(seuilAlerte)) >= 0)
                .map(ClientMapper::toDto)
                .toList();
        return new EncoursDTO(total, impayes, clients.size(), proches);
    }
}
