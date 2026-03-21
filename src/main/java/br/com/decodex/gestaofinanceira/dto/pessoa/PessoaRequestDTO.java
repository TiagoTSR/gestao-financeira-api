package br.com.decodex.gestaofinanceira.dto.pessoa;

import br.com.decodex.gestaofinanceira.dto.endereco.EnderecoDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PessoaRequestDTO(

        @NotBlank
        String nome,

        EnderecoDTO endereco,

        @NotNull
        Boolean ativo
) {}

