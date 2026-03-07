package br.com.decodex.gestaofinanceira.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class PessoaRequestDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("Deve invalidar quando nome for nulo ou em branco")
    void shouldInvalidateBlankNome() {
        PessoaRequestDTO dto = new PessoaRequestDTO("", null, true);
        Set<ConstraintViolation<PessoaRequestDTO>> violations = validator.validate(dto);
        assertThat(violations).isNotEmpty();
    }
}