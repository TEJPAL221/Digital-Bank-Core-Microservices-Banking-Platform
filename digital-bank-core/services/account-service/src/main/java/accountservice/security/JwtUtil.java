package accountservice.security;

import accountservice.model.Role;
import accountservice.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

  @Value("${jwt.secret:secret-key-please-change}")
  private String secret;

  @Value("${jwt.expiration-ms:86400000}") // 1 day
  private long expirationMs;

  @PostConstruct
  public void init() {
    // Optionally encode the secret or load from env/vault
  }

  public String generateToken(User user) {
    var roles = user.getRoles().stream()
        .map(Role::getName)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .setSubject(user.getUsername())
        .claim("roles", roles)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Claims parseToken(String token) {
    return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody();
  }
}
