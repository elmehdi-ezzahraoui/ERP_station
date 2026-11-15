package ma.emsi.station.authentification.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.authentification.dtos.UtilisateurDTO;
import ma.emsi.station.authentification.dtos.UtilisateurRequest;
import ma.emsi.station.authentification.dtos.UtilisateurUpdateRequest;
import ma.emsi.station.authentification.entities.Utilisateur;
import ma.emsi.station.authentification.exceptions.BusinessException;
import ma.emsi.station.authentification.mappers.UtilisateurMapper;
import ma.emsi.station.authentification.repositories.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Gestion des comptes (reservee a l'administrateur via la Gateway). */
@Service
@RequiredArgsConstructor
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UtilisateurDTO> findAll() {
        return utilisateurRepository.findAll().stream().map(UtilisateurMapper::toDto).toList();
    }

    public UtilisateurDTO create(UtilisateurRequest r) {
        if (utilisateurRepository.existsByUsername(r.username())) {
            throw BusinessException.conflict("Le nom d'utilisateur " + r.username() + " existe deja");
        }
        Utilisateur u = Utilisateur.builder()
                .username(r.username())
                .motDePasse(passwordEncoder.encode(r.motDePasse()))
                .nomComplet(r.nomComplet())
                .role(r.role())
                .build();
        return UtilisateurMapper.toDto(utilisateurRepository.save(u));
    }

    public UtilisateurDTO update(Long id, UtilisateurUpdateRequest r) {
        Utilisateur u = utilisateurRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Utilisateur introuvable : " + id));
        u.setNomComplet(r.nomComplet());
        u.setRole(r.role());
        u.setActif(r.actif());
        return UtilisateurMapper.toDto(u);
    }
}
