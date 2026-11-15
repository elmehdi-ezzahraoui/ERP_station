package ma.emsi.station.authentification.dtos;

import ma.emsi.station.authentification.enums.Role;

public record LoginResponse(String accessToken, String tokenType, long expiresIn, String username,
                            String nomComplet, Role role) {
}
