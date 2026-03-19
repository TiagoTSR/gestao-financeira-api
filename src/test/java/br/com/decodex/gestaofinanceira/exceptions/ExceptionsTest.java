package br.com.decodex.gestaofinanceira.exceptions;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionsTest {

    // -------------------------
    // ResourceNotFoundException
    // -------------------------

    @Test
    @DisplayName("ResourceNotFoundException deve herdar de RuntimeException")
    void resourceNotFoundExceptionDeveSerRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso não encontrado");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Recurso não encontrado");
    }

    @Test
    @DisplayName("ResourceNotFoundException deve preservar a mensagem")
    void resourceNotFoundExceptionDeveTerMensagem() {
        String mensagem = "Categoria não encontrada: 99";
        ResourceNotFoundException ex = new ResourceNotFoundException(mensagem);

        assertThat(ex.getMessage()).isEqualTo(mensagem);
    }

    // -------------------------
    // InvalidTokenException
    // -------------------------

    @Test
    @DisplayName("InvalidTokenException deve herdar de RuntimeException")
    void invalidTokenExceptionDeveSerRuntimeException() {
        InvalidTokenException ex = new InvalidTokenException("Token inválido");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Token inválido");
    }

    @Test
    @DisplayName("InvalidTokenException deve preservar a mensagem")
    void invalidTokenExceptionDeveTerMensagem() {
        String mensagem = "Refresh token expirado ou inválido";
        InvalidTokenException ex = new InvalidTokenException(mensagem);

        assertThat(ex.getMessage()).isEqualTo(mensagem);
    }

    // -------------------------
    // ApiError
    // -------------------------

    @Test
    @DisplayName("ApiError deve armazenar todos os campos corretamente")
    void apiErrorDeveTerTodosOsCampos() {
        Instant agora = Instant.now();
        ApiError error = new ApiError(agora, 404, "Not Found", "Recurso não encontrado", "/api/test");

        assertThat(error.getTimestamp()).isEqualTo(agora);
        assertThat(error.getStatus()).isEqualTo(404);
        assertThat(error.getError()).isEqualTo("Not Found");
        assertThat(error.getMessage()).isEqualTo("Recurso não encontrado");
        assertThat(error.getPath()).isEqualTo("/api/test");
        assertThat(error.getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("ApiError deve permitir setar fieldErrors")
    void apiErrorDevePermitirFieldErrors() {
        ApiError error = new ApiError(
                Instant.now(), 400, "Validation error", "Campos inválidos", "/api/test");

        Map<String, String> fieldErrors = new LinkedHashMap<>();
        fieldErrors.put("nome", "não pode ser vazio");
        fieldErrors.put("email", "formato inválido");
        error.setFieldErrors(fieldErrors);

        assertThat(error.getFieldErrors()).hasSize(2);
        assertThat(error.getFieldErrors().get("nome")).isEqualTo("não pode ser vazio");
        assertThat(error.getFieldErrors().get("email")).isEqualTo("formato inválido");
    }

    @Test
    @DisplayName("ApiError deve permitir construtor vazio e setters")
    void apiErrorConstrutorVazioDevePermitirSetters() {
        ApiError error = new ApiError();
        Instant agora = Instant.now();

        error.setTimestamp(agora);
        error.setStatus(500);
        error.setError("Internal server error");
        error.setMessage("An unexpected error occurred");
        error.setPath("/api/test");

        assertThat(error.getTimestamp()).isEqualTo(agora);
        assertThat(error.getStatus()).isEqualTo(500);
        assertThat(error.getError()).isEqualTo("Internal server error");
        assertThat(error.getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(error.getPath()).isEqualTo("/api/test");
    }
}