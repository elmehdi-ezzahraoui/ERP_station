package ma.emsi.station.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Filtre global : verifie le JWT, applique la matrice des roles,
 * puis propage l'identite aux microservices via les en-tetes X-User / X-Role.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (HttpMethod.OPTIONS.equals(method) || AccessRules.isPublic(path)) {
            return chain.filter(exchange);
        }
        if (AccessRules.isInternal(method, path)) {
            return reject(exchange, HttpStatus.FORBIDDEN, "Endpoint interne non expose");
        }

        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            return reject(exchange, HttpStatus.UNAUTHORIZED, "Token JWT manquant");
        }

        Claims claims;
        try {
            claims = jwtService.parse(header.substring(7));
        } catch (JwtException | IllegalArgumentException e) {
            return reject(exchange, HttpStatus.UNAUTHORIZED, "Token JWT invalide ou expire");
        }

        String role = claims.get("role", String.class);
        if (!AccessRules.isAllowed(role, method, path)) {
            return reject(exchange, HttpStatus.FORBIDDEN, "Acces refuse pour le role " + role);
        }

        ServerHttpRequest mutated = request.mutate()
                .headers(h -> {
                    h.remove("X-User");
                    h.remove("X-Role");
                })
                .header("X-User", claims.getSubject())
                .header("X-Role", role)
                .build();
        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private Mono<Void> reject(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":" + status.value() + ",\"message\":\"" + message + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
