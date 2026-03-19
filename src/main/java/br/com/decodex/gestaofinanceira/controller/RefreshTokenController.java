package br.com.decodex.gestaofinanceira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.decodex.gestaofinanceira.dto.AuthData;
import br.com.decodex.gestaofinanceira.dto.RefreshResponse;
import br.com.decodex.gestaofinanceira.service.RefreshTokenService;
import br.com.decodex.gestaofinanceira.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/refresh-token")
public class RefreshTokenController {

    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private CookieUtil cookieUtil;

    @PostMapping
    public ResponseEntity<?> refresh(HttpServletRequest request) {
        String token = cookieUtil.getCookieValue(request, "refreshToken");
        
        if (token == null) {
            return ResponseEntity.status(401).body("Refresh token ausente");
        }

        AuthData authData = refreshTokenService.renovarTokens(token);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(authData.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(authData.refreshToken()).toString())
                .body(new RefreshResponse("Token renovado com sucesso"));
    }
}