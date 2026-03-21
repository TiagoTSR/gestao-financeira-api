package br.com.decodex.gestaofinanceira.dto.pessoa;

import br.com.decodex.gestaofinanceira.dto.endereco.EnderecoDTO;

public record PessoaResponseDTO(
        Long id,
        String nome,
        EnderecoDTO endereco,
        Boolean ativo
) {}
