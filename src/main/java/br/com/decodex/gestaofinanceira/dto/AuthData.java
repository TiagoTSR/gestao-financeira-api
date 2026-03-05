package br.com.decodex.gestaofinanceira.dto;

public record AuthData(String token, String refreshToken, UsuarioResponse usuario) {}