package br.com.decodex.gestaofinanceira.dto;

public record PessoaResponseDTO(
        Long id,
        String nome,
        EnderecoDTO endereco,
        Boolean ativo
) {}
