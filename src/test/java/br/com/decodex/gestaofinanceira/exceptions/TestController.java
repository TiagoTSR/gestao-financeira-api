package br.com.decodex.gestaofinanceira.exceptions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@RestController
public class TestController {

	@GetMapping("/test/not-found")
    public void notFound() {
        throw new ResourceNotFoundException("Recurso não encontrado");
    }

    @GetMapping("/test/invalid-token")
    public void invalidToken() {
        throw new InvalidTokenException("Token inválido");
    }

    @GetMapping("/test/illegal-argument")
    public void illegalArgument() {
        throw new IllegalArgumentException("Argumento inválido");
    }

    @GetMapping("/test/generic")
    public void generic() {
        throw new RuntimeException("Erro inesperado");
    }

    @GetMapping("/test/bad-credentials")
    public void badCredentials() {
        throw new BadCredentialsException("Credenciais inválidas");
    }

    @GetMapping("/test/disabled")
    public void disabled() {
        throw new DisabledException("Conta desativada");
    }

    @GetMapping("/test/forbidden")
    public void forbidden() {
        throw new AuthorizationDeniedException("Acesso negado",
                () -> false);
    }

    @GetMapping("/test/constraint-violation")
    public void constraintViolation() {
        Set<ConstraintViolation<?>> violations = new LinkedHashSet<>();

        jakarta.validation.Validation.buildDefaultValidatorFactory()
                .getValidator()
                .validate(new CategoriaRequestDTO("")) // @NotBlank dispara a violação
                .forEach(violations::add);
        throw new ConstraintViolationException(violations);
    }

    @PostMapping(value = "/test/malformed-json", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void validBody(@Valid @RequestBody CategoriaRequestDTO dto) {
    }
}