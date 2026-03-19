package br.com.decodex.gestaofinanceira.integration;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.decodex.gestaofinanceira.config.BaseIntegrationTest;
import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;

public class RefreshTokenControllerIT extends BaseIntegrationTest {

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

        if (usuarioRepository.findByUsername("user-test").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("user-test");
            usuario.setPassword(passwordEncoder.encode("password"));
            usuario.setRole("USER");
            usuarioRepository.save(usuario);
        }
    }

    @Test
    @DisplayName("Deve invalidar o token antigo após a rotação")
    void shouldRotateTokensAndInvalidateOldOne() throws Exception {
        MvcResult loginRes = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("user-test", "password"))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie firstRefreshCookie = extractCookie(loginRes, "refreshToken");
        assertNotNull(firstRefreshCookie, "Cookie refreshToken não encontrado");

        MvcResult refreshRes = mockMvc.perform(post("/api/refresh-token")
                .cookie(firstRefreshCookie))
                .andExpect(status().isOk())
                .andReturn();

        Cookie secondRefreshCookie = extractCookie(refreshRes, "refreshToken");
        assertNotNull(secondRefreshCookie, "Novo cookie refreshToken não encontrado");
        assertNotEquals(firstRefreshCookie.getValue(), secondRefreshCookie.getValue(),
                "O refresh token deve ser diferente após a rotação");
    }

    @Test
    @DisplayName("Deve retornar 401 quando o refresh token estiver ausente")
    void shouldReturn401WhenCookieIsMissing() throws Exception {
        mockMvc.perform(post("/api/refresh-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 no reuso de token já rotacionado")
    void shouldReturn401WhenTokenIsReused() throws Exception {
        MvcResult loginRes = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("user-test", "password"))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie oldCookie = extractCookie(loginRes, "refreshToken");

        // Primeiro uso — deve funcionar
        mockMvc.perform(post("/api/refresh-token")
                .cookie(oldCookie))
                .andExpect(status().isOk());

        // Reuso do token antigo — deve falhar
        mockMvc.perform(post("/api/refresh-token")
                .cookie(oldCookie))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar 401 com token adulterado")
    void shouldReturn401WhenTokenIsTampered() throws Exception {
        Cookie fakeCookie = new Cookie("refreshToken", "token.adulterado.invalido");

        mockMvc.perform(post("/api/refresh-token")
                .cookie(fakeCookie))
                .andExpect(status().isUnauthorized());
    }

    private Cookie extractCookie(MvcResult result, String name) {
        for (String header : result.getResponse().getHeaders("Set-Cookie")) {
            if (header.startsWith(name + "=")) {
                String value = header.split("=")[1].split(";")[0];
                return new Cookie(name, value);
            }
        }
        return null;
    }
}