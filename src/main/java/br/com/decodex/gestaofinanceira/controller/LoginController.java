package br.com.decodex.gestaofinanceira.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.decodex.gestaofinanceira.dto.AuthData;
import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.LoginResponse;
import br.com.decodex.gestaofinanceira.service.LoginService;
import br.com.decodex.gestaofinanceira.util.CookieUtil;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired private LoginService loginService;
    @Autowired private CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> logar(
            @RequestBody LoginRequest login,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {

        AuthData authData = loginService.autenticar(login, userAgent);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(authData.accessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(authData.refreshToken()).toString())
                .body(new LoginResponse(authData.usuario(), authData.accessToken()));
    }
}