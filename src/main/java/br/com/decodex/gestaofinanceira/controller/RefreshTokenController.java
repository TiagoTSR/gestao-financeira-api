package br.com.decodex.gestaofinanceira.controller;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.RefreshResponse;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/refresh-token")
public class RefreshTokenController {

    @Autowired
    private JwtServiceGenerator jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token não encontrado");
        }

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenWithUsuario(refreshToken);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Refresh token inválido");
        }

        RefreshToken refreshTokenEntity = tokenOpt.get();
        Usuario usuario = refreshTokenEntity.getUsuario();

        if (refreshTokenEntity.getDataExpiracao().isBefore(java.time.LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshTokenEntity);
            return ResponseEntity.status(401).body("Refresh token expirado");
        }

        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            refreshTokenRepository.delete(refreshTokenEntity);
            return ResponseEntity.status(401).body("Refresh token inválido ou corrompido");
        }

        String newAccessToken = jwtService.generateToken(usuario);

        String newRefreshToken = jwtService.generateRefreshToken(usuario);
        refreshTokenEntity.setToken(newRefreshToken);
        refreshTokenEntity.setDataExpiracao(jwtService.extractExpirationAsLocalDateTime(newRefreshToken));
        refreshTokenRepository.save(refreshTokenEntity);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(new RefreshResponse("Token renovado com sucesso"));
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> "refreshToken".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}