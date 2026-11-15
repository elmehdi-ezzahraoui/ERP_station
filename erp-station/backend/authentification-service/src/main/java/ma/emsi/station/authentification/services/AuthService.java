package ma.emsi.station.authentification.services;

import lombok.RequiredArgsConstructor;
import ma.emsi.station.authentification.dtos.ChangementMotDePasseRequest;
import ma.emsi.station.authentification.dtos.LoginRequest;
import ma.emsi.station.authentification.dtos.LoginResponse;
import ma.emsi.station.authentification.dtos.UtilisateurDTO;
import ma.emsi.station.authentification.entities.Utilisateur;
import ma.emsi.station.authentification.exceptions.BusinessException;
import ma.emsi.station.authentification.mappers.UtilisateurMapper;
import ma.emsi.station.authentification.repositories.UtilisateurRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest r) {
        Utilisateur u = utilisateurRepository.findByUsername(r.username())
                .filter(x -> passwordEncoder.matches(r.password(), x.getMotDePasse()))
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects"));
        if (!u.isActif()) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Compte desactive");
        }
        return new LoginResponse(jwtService.generateToken(u), "Bearer", jwtService.getExpirationSeconds(),
                u.getUsername(), u.getNomComplet(), u.getRole());
    }

    @Transactional(readOnly = true)
    public UtilisateurDTO moi(String username) {
        return UtilisateurMapper.toDto(getByUsername(username));
    }

    public void changerMotDePasse(String username, ChangementMotDePasseRequest r) {
        Utilisateur u = getByUsername(username);
        if (!passwordEncoder.matches(r.ancienMotDePasse(), u.getMotDePasse())) {
            throw BusinessException.badRequest("Ancien mot de passe incorrect");
        }
        u.setMotDePasse(passwordEncoder.encode(r.nouveauMotDePasse()));
    }

    private Utilisateur getByUsername(String username) {
        if (username == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Utilisateur non identifie");
        }
        return utilisateurRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("Utilisateur introuvable : " + username));
    }
}
