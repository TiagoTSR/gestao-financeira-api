package br.com.decodex.gestaofinanceira.util;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class CookieUtil {
    public ResponseCookie createAccessTokenCookie(String token) {
        return createCookie("accessToken", token, Duration.ofHours(1));
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return createCookie("refreshToken", token, Duration.ofDays(7));
    }

    private ResponseCookie createCookie(String name, String value, Duration duration) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false) // Mude para true em produção (HTTPS)
                .path("/")
                .maxAge(duration)
                .sameSite("Lax")
                .build();
    }

    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }
}