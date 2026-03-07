package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CategoriaResponseDTOTest {

    @Test
    @DisplayName("Deve criar um Record de resposta com sucesso e manter a integridade dos dados")
    void shouldCreateResponseDTOAndMaintainDataIntegrity() {
        // Dados de teste
        Long expectedId = 1L;
        String expectedNome = "Alimentação";

        // Instanciação do Record
        CategoriaResponseDTO dto = new CategoriaResponseDTO(expectedId, expectedNome);

        // Asserts de valores
        assertThat(dto.id()).isEqualTo(expectedId);
        assertThat(dto.nome()).isEqualTo(expectedNome);
    }

    @Test
    @DisplayName("Deve garantir a imutabilidade e igualdade do Record")
    void shouldEnsureEqualityAndImmutability() {
        CategoriaResponseDTO dto1 = new CategoriaResponseDTO(1L, "Lazer");
        CategoriaResponseDTO dto2 = new CategoriaResponseDTO(1L, "Lazer");
        CategoriaResponseDTO dto3 = new CategoriaResponseDTO(2L, "Saúde");

        // Records implementam equals() e hashCode() automaticamente baseados nos campos
        assertThat(dto1).isEqualTo(dto2); 
        assertThat(dto1).isNotEqualTo(dto3);
        
        // Verifica se o hashCode é consistente
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
    }

    @Test
    @DisplayName("Deve validar a implementação automática do toString")
    void shouldHaveValidToStringImplementation() {
        CategoriaResponseDTO dto = new CategoriaResponseDTO(1L, "Transporte");
        
        String toString = dto.toString();
        
        assertThat(toString)
            .contains("id=1")
            .contains("nome=Transporte")
            .contains("CategoriaResponseDTO");
    }
}