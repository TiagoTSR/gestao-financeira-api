package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class LancamentoRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar com sucesso quando todos os campos obrigatórios estão presentes")
    void shouldNotHaveViolationsWhenDTOIsValid() {
        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                "Aluguel",
                LocalDate.now(),
                null,
                new BigDecimal("1200.00"),
                "Pagamento mensal",
                TipoLancamento.DESPESA,
                1L,
                1L,
                null,   // anexo
                null    // urlAnexo
        );

        Set<ConstraintViolation<LancamentoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar quando campos obrigatórios forem nulos")
    void shouldHaveViolationsWhenRequiredFieldsAreNull() {
        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                null,   // descricao       @NotBlank
                null,   // dataVencimento  @NotNull
                null,   // dataPagamento
                null,   // valor           @NotNull
                null,   // observacao
                null,   // tipo            @NotNull
                null,   // categoriaId     @NotNull
                null,   // pessoaId        @NotNull
                null,   // anexo
                null    // urlAnexo
        );

        Set<ConstraintViolation<LancamentoRequestDTO>> violations = validator.validate(dto);
        // 6 campos anotados: descricao, dataVencimento, valor, tipo, categoriaId, pessoaId
        assertThat(violations).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    @DisplayName("Deve invalidar quando a descrição estiver em branco")
    void shouldHaveViolationWhenDescricaoIsBlank() {
        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                "   ",                      // descricao em branco
                LocalDate.now(),
                null,
                new BigDecimal("100.00"),
                null,
                TipoLancamento.RECEITA,
                1L,
                1L,
                null,                       // anexo
                null                        // urlAnexo
        );

        Set<ConstraintViolation<LancamentoRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("descricao"));
    }
}