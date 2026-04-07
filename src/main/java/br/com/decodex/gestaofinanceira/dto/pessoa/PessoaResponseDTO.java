package br.com.decodex.gestaofinanceira.dto.pessoa;

import java.util.List;

import br.com.decodex.gestaofinanceira.dto.contato.ContatoResponseDTO;
import br.com.decodex.gestaofinanceira.dto.endereco.EnderecoDTO;

public record PessoaResponseDTO(
        Long id,
        String nome,
        EnderecoDTO endereco,
        Boolean ativo,
        List<ContatoResponseDTO> contatos
) {}
