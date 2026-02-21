package br.com.decodex.gestaofinanceira.dto;

import br.com.decodex.gestaofinanceira.model.Usuario;

public record UsuarioResponse(
	    Long id,
	    String username,
	    String role
	) {
	    public UsuarioResponse(Usuario usuario) {
	        this(usuario.getId(), usuario.getUsername(), usuario.getRole());
	    }
	}