package ma.emsi.station.achat.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.achat.dtos.FournisseurDTO;
import ma.emsi.station.achat.entities.Fournisseur;
import ma.emsi.station.achat.exceptions.BusinessException;
import ma.emsi.station.achat.mappers.AchatMapper;
import ma.emsi.station.achat.repositories.FournisseurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FournisseurService {

    private final FournisseurRepository fournisseurRepository;

    @Transactional(readOnly = true)
    public List<FournisseurDTO> findAll() {
        return fournisseurRepository.findAll().stream().map(AchatMapper::toDto).toList();
    }

    public Fournisseur getEntity(Long id) {
        return fournisseurRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Fournisseur introuvable : " + id));
    }

    public FournisseurDTO create(FournisseurDTO dto) {
        Fournisseur f = Fournisseur.builder().nom(dto.nom()).telephone(dto.telephone())
                .email(dto.email()).adresse(dto.adresse()).build();
        return AchatMapper.toDto(fournisseurRepository.save(f));
    }

    public FournisseurDTO update(Long id, FournisseurDTO dto) {
        Fournisseur f = getEntity(id);
        f.setNom(dto.nom());
        f.setTelephone(dto.telephone());
        f.setEmail(dto.email());
        f.setAdresse(dto.adresse());
        return AchatMapper.toDto(f);
    }
}
