package br.com.decodex.gestaofinanceira.dto;

public record LoginResponse(
	    String token,
	    UsuarioResponse usuario
	) {}