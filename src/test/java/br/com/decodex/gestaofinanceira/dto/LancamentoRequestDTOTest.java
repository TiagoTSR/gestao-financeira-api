package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
                1L
        );

        Set<ConstraintViolation<LancamentoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar quando campos obrigatórios forem nulos")
    void shouldHaveViolationsWhenRequiredFieldsAreNull() {
        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                null, 
                null, 
                null,
                null,
                null,
                null,
                null,
                null 
        );

        Set<ConstraintViolation<LancamentoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSizeGreaterThanOrEqualTo(6);
    }

    @Test
    @DisplayName("Deve invalidar quando a descrição estiver em branco")
    void shouldHaveViolationWhenDescricaoIsBlank() {
        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                "   ",
                LocalDate.now(),
                null,
                new BigDecimal("100.00"),
                null,
                TipoLancamento.RECEITA,
                1L,
                1L
        );

        Set<ConstraintViolation<LancamentoRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("descricao"));
    }
}