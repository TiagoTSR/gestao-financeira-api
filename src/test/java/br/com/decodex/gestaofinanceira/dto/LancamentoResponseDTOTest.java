package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.decodex.gestaofinanceira.model.TipoLancamento;

class LancamentoResponseDTOTest {

    @Test
    @DisplayName("Deve garantir a integridade dos dados no Record de Resposta")
    void shouldMaintainDataIntegrity() {
        LocalDate data = LocalDate.of(2024, 5, 20);
        BigDecimal valor = new BigDecimal("1500.50");

        LancamentoResponseDTO dto = new LancamentoResponseDTO(
                1L, "Freelance", data, null, valor, "Nota 1",
                TipoLancamento.RECEITA, 2L, "Desenvolvimento", 3L, "Empresa X"
        );

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.descricao()).isEqualTo("Freelance");
        assertThat(dto.categoriaNome()).isEqualTo("Desenvolvimento");
        assertThat(dto.pessoaNome()).isEqualTo("Empresa X");
        assertThat(dto.valor()).isEqualByComparingTo("1500.50");
    }
}