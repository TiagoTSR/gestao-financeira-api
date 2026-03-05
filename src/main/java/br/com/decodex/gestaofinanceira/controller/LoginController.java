package br.com.decodex.gestaofinanceira.controller;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.AuthData;
import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.LoginResponse;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.service.LoginService;
import br.com.decodex.gestaofinanceira.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService loginService;
    
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtServiceGenerator jwtService;

    @PostMapping
    public ResponseEntity<LoginResponse> logar(
            @RequestBody LoginRequest login,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletResponse response) {

        AuthData authData = loginService.autenticar(login, userAgent);

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", authData.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", authData.refreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofDays(7))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return ResponseEntity.ok(new LoginResponse(authData.usuario()));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String refreshTokenValue = extractCookie(request, "refreshToken");

        if (refreshTokenValue == null) {
            return ResponseEntity.status(401).body("Refresh Token ausente");
        }

        return refreshTokenService.findByToken(refreshTokenValue)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(usuario -> {
                    String novoAccessToken = jwtService.generateToken(usuario);
                    ResponseCookie jwtCookie = ResponseCookie.from("accessToken", novoAccessToken)
                            .httpOnly(true).secure(false).path("/").maxAge(Duration.ofMinutes(15)).sameSite("Lax").build();
                    
                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                            .body("Token renovado com sucesso");
                })
                .orElseThrow(() -> new RuntimeException("Refresh token inválido ou expirado!"));
    }

    private String extractCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }
}