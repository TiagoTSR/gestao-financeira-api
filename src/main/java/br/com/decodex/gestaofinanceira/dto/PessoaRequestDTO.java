package br.com.decodex.gestaofinanceira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PessoaRequestDTO(

        @NotBlank
        String nome,

        EnderecoDTO endereco,

        @NotNull
        Boolean ativo
) {}

