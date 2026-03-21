package br.com.decodex.gestaofinanceira.dto.login;

import br.com.decodex.gestaofinanceira.dto.usuario.UsuarioResponse;

public record LoginResponse(UsuarioResponse usuario, String token) { }