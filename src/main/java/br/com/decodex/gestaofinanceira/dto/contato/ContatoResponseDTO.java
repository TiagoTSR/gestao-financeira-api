package br.com.decodex.gestaofinanceira.dto.contato;

public record ContatoResponseDTO(
		Long id,
		String nome,
		String email,
	    String telefone
) {}
