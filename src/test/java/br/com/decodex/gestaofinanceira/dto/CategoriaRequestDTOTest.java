package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CategoriaRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar com sucesso quando o nome for válido")
    void shouldNotHaveViolationsWhenNomeIsValid() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Alimentação");

        Set<ConstraintViolation<CategoriaRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve invalidar quando o nome for nulo ou em branco")
    void shouldHaveViolationsWhenNomeIsBlank() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("   ");

        Set<ConstraintViolation<CategoriaRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).isNotBlank();
    }

    @Test
    @DisplayName("Deve invalidar quando o nome for muito curto")
    void shouldHaveViolationsWhenNomeIsTooShort() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Oi");

        Set<ConstraintViolation<CategoriaRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("nome");
    }

    @Test
    @DisplayName("Deve invalidar quando o nome exceder 50 caracteres")
    void shouldHaveViolationsWhenNomeIsTooLong() {
        String nomeLongo = "A".repeat(51);
        CategoriaRequestDTO dto = new CategoriaRequestDTO(nomeLongo);

        Set<ConstraintViolation<CategoriaRequestDTO>> violations = validator.validate(dto);

        assertThat(violations).isNotEmpty();
    }
}