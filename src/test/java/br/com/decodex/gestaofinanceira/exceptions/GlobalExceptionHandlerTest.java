package br.com.decodex.gestaofinanceira.exceptions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;

@WebMvcTest(TestController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtServiceGenerator jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 para ResourceNotFoundException")
    void handleResourceNotFoundShouldReturn404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource not found"))
                .andExpect(jsonPath("$.message").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.path").value("/test/not-found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 401 para InvalidTokenException")
    void handleInvalidTokenShouldReturn401() throws Exception {
        mockMvc.perform(get("/test/invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Token inválido"))
                .andExpect(jsonPath("$.path").value("/test/invalid-token"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 para IllegalArgumentException")
    void handleIllegalArgumentShouldReturn400() throws Exception {
        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Argumento inválido"))
                .andExpect(jsonPath("$.path").value("/test/illegal-argument"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 500 para Exception genérica")
    void handleGenericShouldReturn500() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal server error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/test/generic"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    @WithMockUser
    @DisplayName("Deve retornar 401 para BadCredentialsException")
    void handleBadCredentialsShouldReturn401() throws Exception {
        mockMvc.perform(get("/test/bad-credentials"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Credenciais inválidas"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 401 para DisabledException")
    void handleDisabledShouldReturn401() throws Exception {
        mockMvc.perform(get("/test/disabled"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Conta desativada"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 403 para AuthorizationDeniedException")
    void handleAuthorizationDeniedShouldReturn403() throws Exception {
        mockMvc.perform(get("/test/forbidden"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Acesso negado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 para JSON malformado")
    void handleMalformedJsonShouldReturn400() throws Exception {
        mockMvc.perform(post("/test/malformed-json")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ json invalido }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Malformed JSON"))
                .andExpect(jsonPath("$.message").value("Request body is invalid or unreadable"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 405 para método HTTP não suportado")
    void handleMethodNotSupportedShouldReturn405() throws Exception {
        // Tenta chamar POST em um endpoint que só aceita GET
        mockMvc.perform(post("/test/not-found")
                .with(csrf()))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.error").value("Method Not Allowed"))
                .andExpect(jsonPath("$.message").value("Método HTTP não suportado para este endpoint"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 com fieldErrors para MethodArgumentNotValidException")
    void handleMethodArgumentNotValidShouldReturn400WithFieldErrors() throws Exception {
        // Envia nome vazio para disparar validação do @NotBlank
        mockMvc.perform(post("/test/malformed-json")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    @WithMockUser
    @DisplayName("Deve retornar 404 para NoResourceFoundException")
    void handleNoResourceFoundShouldReturn404() throws Exception {
        // Acessa uma rota que não existe — o Spring lança NoResourceFoundException automaticamente
        mockMvc.perform(get("/rota-que-nao-existe-em-lugar-algum"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Endpoint não encontrado"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
    
    @Test
    @WithMockUser
    @DisplayName("Deve retornar 400 com fieldErrors para ConstraintViolationException")
    void handleConstraintViolationShouldReturn400WithFieldErrors() throws Exception {
        mockMvc.perform(get("/test/constraint-violation"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation error"))
                .andExpect(jsonPath("$.message").value("One or more parameters are invalid"))
                .andExpect(jsonPath("$.fieldErrors").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }
}