package br.com.decodex.gestaofinanceira.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.decodex.gestaofinanceira.config.BaseIntegrationTest;
import br.com.decodex.gestaofinanceira.dto.login.LoginRequest;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;

public class LoginControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("admin");
            usuario.setPassword(passwordEncoder.encode("123456"));
            usuario.setRole("ROLE_ADMIN");
            usuarioRepository.save(usuario);
        }
    }

    @Test
    @DisplayName("Deve autenticar com sucesso e retornar cookies HttpOnly")
    void loginShouldReturnCookiesWhenCredentialsAreValid() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "123456");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .header("User-Agent", "Mozilla/5.0"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(cookie().httpOnly("accessToken", true))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(jsonPath("$.usuario.username").value("admin"))
                .andExpect(jsonPath("$.usuario.role").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Deve retornar 401 com credenciais inválidas")
    void loginShouldReturn401WhenCredentialsAreInvalid() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "senha-errada");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 com usuário inexistente")
    void loginShouldReturn401WhenUserDoesNotExist() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nao-existe", "qualquer");

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve renovar o access token usando o refresh token do cookie")
    void refreshShouldWorkWithCookie() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "123456");

        var mvcResult = mockMvc.perform(post("/api/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshCookie = mvcResult.getResponse().getCookie("refreshToken");

        mockMvc.perform(post("/api/refresh-token")
                .with(csrf())
                .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(jsonPath("$.message").value("Token renovado com sucesso"));
    }
}