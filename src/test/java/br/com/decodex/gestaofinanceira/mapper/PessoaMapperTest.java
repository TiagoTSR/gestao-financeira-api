package br.com.decodex.gestaofinanceira.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.decodex.gestaofinanceira.dto.EnderecoDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.model.Endereco;
import br.com.decodex.gestaofinanceira.model.Pessoa;

class PessoaMapperTest {

    private PessoaMapper mapper;

    private EnderecoDTO enderecoDTO;
    private Endereco enderecoEntity;

    @BeforeEach
    void setUp() {
        mapper = new PessoaMapper();

        enderecoDTO = new EnderecoDTO(
                "Rua das Flores", "100", "Apto 1",
                "Centro", "12345-678", "São Paulo", "SP"
        );

        enderecoEntity = new Endereco();
        enderecoEntity.setLogradouro("Rua das Flores");
        enderecoEntity.setNumero("100");
        enderecoEntity.setComplemento("Apto 1");
        enderecoEntity.setBairro("Centro");
        enderecoEntity.setCep("12345-678");
        enderecoEntity.setCidade("São Paulo");
        enderecoEntity.setEstado("SP");
    }

    // -------------------------
    // toEntity
    // -------------------------

    @Test
    @DisplayName("toEntity - Deve mapear todos os campos corretamente")
    void toEntityShouldMapAllFields() {
        PessoaRequestDTO dto = new PessoaRequestDTO("João Silva", enderecoDTO, true);

        Pessoa result = mapper.toEntity(dto);

        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getAtivo()).isTrue();
        assertThat(result.getEndereco()).isNotNull();
        assertThat(result.getEndereco().getLogradouro()).isEqualTo("Rua das Flores");
        assertThat(result.getEndereco().getNumero()).isEqualTo("100");
        assertThat(result.getEndereco().getComplemento()).isEqualTo("Apto 1");
        assertThat(result.getEndereco().getBairro()).isEqualTo("Centro");
        assertThat(result.getEndereco().getCep()).isEqualTo("12345-678");
        assertThat(result.getEndereco().getCidade()).isEqualTo("São Paulo");
        assertThat(result.getEndereco().getEstado()).isEqualTo("SP");
    }

    @Test
    @DisplayName("toEntity - Deve retornar endereço nulo quando DTO de endereço for nulo")
    void toEntityWithNullEnderecoShouldReturnNullEndereco() {
        PessoaRequestDTO dto = new PessoaRequestDTO("João Silva", null, true);

        Pessoa result = mapper.toEntity(dto);

        assertThat(result.getNome()).isEqualTo("João Silva");
        assertThat(result.getEndereco()).isNull();
    }

    // -------------------------
    // updateEntity
    // -------------------------

    @Test
    @DisplayName("updateEntity - Deve atualizar todos os campos da entidade existente")
    void updateEntityShouldOverwriteAllFields() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome("Nome Antigo");
        pessoa.setAtivo(false);
        pessoa.setEndereco(enderecoEntity);

        EnderecoDTO novoEndereco = new EnderecoDTO(
                "Av. Paulista", "1000", null,
                "Bela Vista", "01310-100", "São Paulo", "SP"
        );
        PessoaRequestDTO dto = new PessoaRequestDTO("Nome Novo", novoEndereco, true);

        mapper.updateEntity(pessoa, dto);

        assertThat(pessoa.getNome()).isEqualTo("Nome Novo");
        assertThat(pessoa.getAtivo()).isTrue();
        assertThat(pessoa.getEndereco().getLogradouro()).isEqualTo("Av. Paulista");
        assertThat(pessoa.getEndereco().getNumero()).isEqualTo("1000");
        assertThat(pessoa.getEndereco().getComplemento()).isNull();
    }

    // -------------------------
    // toDTO
    // -------------------------

    @Test
    @DisplayName("toDTO - Deve mapear entidade para DTO corretamente")
    void toDTOShouldMapAllFields() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("Maria Souza");
        pessoa.setAtivo(true);
        pessoa.setEndereco(enderecoEntity);

        PessoaResponseDTO result = mapper.toDTO(pessoa);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("Maria Souza");
        assertThat(result.ativo()).isTrue();
        assertThat(result.endereco()).isNotNull();
        assertThat(result.endereco().logradouro()).isEqualTo("Rua das Flores");
        assertThat(result.endereco().cidade()).isEqualTo("São Paulo");
        assertThat(result.endereco().estado()).isEqualTo("SP");
    }

    @Test
    @DisplayName("toDTO - Deve retornar endereço nulo quando entidade não tiver endereço")
    void toDTOWithNullEnderecoShouldReturnNullEnderecoDTO() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(2L);
        pessoa.setNome("Carlos Lima");
        pessoa.setAtivo(false);
        pessoa.setEndereco(null);

        PessoaResponseDTO result = mapper.toDTO(pessoa);

        assertThat(result.nome()).isEqualTo("Carlos Lima");
        assertThat(result.ativo()).isFalse();
        assertThat(result.endereco()).isNull();
    }
}