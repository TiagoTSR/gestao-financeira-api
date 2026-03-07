package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PessoaResponseDTOTest {

    @Test
    @DisplayName("Deve garantir a integridade dos dados no Record de Resposta com Endereço")
    void shouldMaintainDataIntegrityWithEndereco() {
        // Arrange
        EnderecoDTO endereco = new EnderecoDTO(
            "Rua das Flores", "100", "Apto 1", "Centro", "12345-000", "Vitória", "ES"
        );
        
        Long id = 1L;
        String nome = "Carlos Alberto";
        Boolean ativo = true;

        PessoaResponseDTO dto = new PessoaResponseDTO(id, nome, endereco, ativo);

        assertThat(dto.id()).isEqualTo(id);
        assertThat(dto.nome()).isEqualTo(nome);
        assertThat(dto.ativo()).isTrue();
        
        assertThat(dto.endereco()).isNotNull();
        assertThat(dto.endereco().logradouro()).isEqualTo("Rua das Flores");
        assertThat(dto.endereco().cidade()).isEqualTo("Vitória");
    }

    @Test
    @DisplayName("Deve suportar PessoaResponseDTO com endereço nulo")
    void shouldHandleNullEndereco() {
        PessoaResponseDTO dto = new PessoaResponseDTO(2L, "Maria Sem Casa", null, true);

        assertThat(dto.endereco()).isNull();
        assertThat(dto.nome()).isEqualTo("Maria Sem Casa");
    }

    @Test
    @DisplayName("Deve garantir igualdade entre dois Records com os mesmos dados")
    void shouldEnsureRecordEquality() {
        EnderecoDTO end1 = new EnderecoDTO("Rua A", "1", null, "Bairro", "00000", "Cidade", "UF");
        EnderecoDTO end2 = new EnderecoDTO("Rua A", "1", null, "Bairro", "00000", "Cidade", "UF");

        PessoaResponseDTO p1 = new PessoaResponseDTO(1L, "João", end1, true);
        PessoaResponseDTO p2 = new PessoaResponseDTO(1L, "João", end2, true);

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}