package br.com.decodex.gestaofinanceira.controller;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.LoginResponse;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.service.LoginService;
import br.com.decodex.gestaofinanceira.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

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
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest login) {
        var authData = loginService.autenticar(login);

        var refreshTokenEntity = refreshTokenService.createRefreshToken(authData.usuario().username());

        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", authData.token())
                .httpOnly(true).secure(false).path("/").maxAge(Duration.ofMinutes(15)).sameSite("Lax").build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshTokenEntity.getToken())
                .httpOnly(true).secure(false).path("/api/login/refresh").maxAge(Duration.ofDays(7)).sameSite("Lax").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(authData.usuario()));
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