package br.com.decodex.gestaofinanceira.dto.contato;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ContatoRequestDTO(
		
		Long id,
		
		@NotEmpty
		String nome,
		
		@NotNull
		String email,
		
		@NotEmpty
		String telefone
) {}
