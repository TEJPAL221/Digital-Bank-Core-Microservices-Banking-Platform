package apigateway.security;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("JwtAuth")
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    // Use a strong secret key - replace with your actual secret in secure storage
    private static final String SECRET_KEY = "ngZL34bDMFQizCBRay0rcOs982hmkW5T";

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {}

    @Override
    public org.springframework.cloud.gateway.filter.GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }
            String token = authHeader.substring(7);

            SignedJWT signedJWT;
            Map<String, Object> claims;

            try {
                signedJWT = SignedJWT.parse(token);
            } catch (ParseException e) {
                return unauthorized(exchange);
            }

            // Verify JWT signature
            try {
                JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
                if (!signedJWT.verify(verifier)) {
                    return unauthorized(exchange);
                }
            } catch (Exception e) {
                return unauthorized(exchange);
            }

            // Check token expiration (optional but recommended)
            try {
                Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
                if (expiration == null || expiration.before(new Date())) {
                    return unauthorized(exchange);
                }
            } catch (ParseException e) {
                return unauthorized(exchange);
            }

            try {
                claims = signedJWT.getJWTClaimsSet().getClaims();
            } catch (ParseException e) {
                return unauthorized(exchange);
            }

            String subject = claims.getOrDefault("sub", "").toString();
            Object rolesObj = claims.getOrDefault("roles", List.of());
            String rolesHeader = rolesObj instanceof List
                    ? String.join(",", ((List<?>) rolesObj).stream().map(Object::toString).toList())
                    : rolesObj.toString();

            var mutated = exchange.getRequest().mutate()
                    .header("X-User-Id", subject)
                    .header("X-User-Roles", rolesHeader)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}
