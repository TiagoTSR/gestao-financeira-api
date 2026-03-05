package br.com.decodex.gestaofinanceira.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private final GestaoApiProperty property;

    public JwtServiceGenerator(GestaoApiProperty property) {
        this.property = property;
    }

    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("role", usuario.getRole());
        claims.put("type", "access");

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + property.getJwt().getAccessExpirationMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + property.getJwt().getRefreshExpirationMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public LocalDateTime extractExpirationAsLocalDateTime(String token) {
        Date expiration = extractExpiration(token);
        return Instant.ofEpochMilli(expiration.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String type = claims.get("type", String.class);
            return "refresh".equals(type) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
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
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                property.getJwt().getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }
}