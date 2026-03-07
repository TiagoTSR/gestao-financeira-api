package br.com.decodex.gestaofinanceira.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.decodex.gestaofinanceira.dto.EnderecoDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.model.Pessoa;

class PessoaMapperTest {

    private PessoaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new PessoaMapper();
    }

    @Test
    @DisplayName("Deve converter PessoaRequestDTO com Endereco para Entidade")
    void toEntityShouldMapNestedEndereco() {
        EnderecoDTO enderecoDTO = new EnderecoDTO("Rua A", "123", null, "Centro", "12345-678", "SP", "SP");
        PessoaRequestDTO request = new PessoaRequestDTO("Marcus", enderecoDTO, true);

        Pessoa result = mapper.toEntity(request);

        assertThat(result.getNome()).isEqualTo("Marcus");
        assertThat(result.getEndereco()).isNotNull();
        assertThat(result.getEndereco().getLogradouro()).isEqualTo("Rua A");
    }

    @Test
    @DisplayName("Deve converter Entidade para PessoaResponseDTO e lidar com Endereco nulo")
    void toDTOShowHandleNullEndereco() {
        Pessoa entity = new Pessoa();
        entity.setId(1L);
        entity.setNome("Sem Endereço");
        entity.setEndereco(null);

        PessoaResponseDTO result = mapper.toDTO(entity);

        assertThat(result.endereco()).isNull();
        assertThat(result.nome()).isEqualTo("Sem Endereço");
    }
}