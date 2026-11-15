package ma.emsi.station.achat.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record FournisseurDTO(Long id, @NotBlank String nom, String telephone, @Email String email, String adresse) {
}
