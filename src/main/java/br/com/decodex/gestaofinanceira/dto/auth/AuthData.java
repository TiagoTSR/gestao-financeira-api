package br.com.decodex.gestaofinanceira.dto.auth;

import br.com.decodex.gestaofinanceira.dto.usuario.UsuarioResponse;

public record AuthData(String accessToken, String refreshToken, UsuarioResponse usuario) {}