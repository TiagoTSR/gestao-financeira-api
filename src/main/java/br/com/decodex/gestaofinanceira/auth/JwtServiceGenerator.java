package br.com.decodex.gestaofinanceira.auth;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.decodex.gestaofinanceira.config.property.GestaoApiProperty;
import br.com.decodex.gestaofinanceira.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceGenerator {

    private static final int HORAS_EXPIRACAO_TOKEN = 1;
    private final GestaoApiProperty property;

    public JwtServiceGenerator(GestaoApiProperty property) {
        this.property = property;
    }

    public String generateToken(Usuario usuario) {
       
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("role", usuario.getRole());

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000L * HORAS_EXPIRACAO_TOKEN))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSigningKey() {
  
        return Keys.hmacShaKeyFor(
                property.getJwt().getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }
}