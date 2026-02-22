package br.com.decodex.gestaofinanceira.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.LoginResponse;
import br.com.decodex.gestaofinanceira.service.LoginService;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponse> logar(@RequestBody LoginRequest login) {

    	var authData = loginService.autenticar(login);

        ResponseCookie jwtCookie = ResponseCookie.from("accessToken", authData.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new LoginResponse(authData.usuario()));
    }
}