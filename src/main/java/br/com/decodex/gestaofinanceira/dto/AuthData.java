package br.com.decodex.gestaofinanceira.dto;

public record AuthData(String accessToken, String refreshToken, UsuarioResponse usuario) {}