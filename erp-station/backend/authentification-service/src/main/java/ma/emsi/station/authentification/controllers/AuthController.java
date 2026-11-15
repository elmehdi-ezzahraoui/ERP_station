package ma.emsi.station.authentification.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ma.emsi.station.authentification.dtos.ChangementMotDePasseRequest;
import ma.emsi.station.authentification.dtos.LoginRequest;
import ma.emsi.station.authentification.dtos.LoginResponse;
import ma.emsi.station.authentification.dtos.UtilisateurDTO;
import ma.emsi.station.authentification.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Public : renvoie le JWT a utiliser dans l'en-tete Authorization: Bearer ... */
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Utilisateur connecte (X-User est ajoute par la Gateway a partir du JWT). */
    @GetMapping("/moi")
    public UtilisateurDTO moi(@RequestHeader(value = "X-User", required = false) String username) {
        return authService.moi(username);
    }

    @PutMapping("/mot-de-passe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changerMotDePasse(@RequestHeader(value = "X-User", required = false) String username,
                                  @Valid @RequestBody ChangementMotDePasseRequest request) {
        authService.changerMotDePasse(username, request);
    }
}
