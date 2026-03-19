package br.com.decodex.gestaofinanceira.integration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
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
import br.com.decodex.gestaofinanceira.repository.RefreshTokenRepository;
import br.com.decodex.gestaofinanceira.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;

public class LogoutControllerIT extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());

        if (usuarioRepository.findByUsername("logout-user").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("logout-user");
            usuario.setPassword(passwordEncoder.encode("password"));
            usuario.setRole("ROLE_USER");
            usuarioRepository.save(usuario);
        }
    }

    @Test
    @DisplayName("Deve realizar logout, limpar cookies e remover refresh token do banco")
    void logoutShouldClearCookiesAndDeleteTokenFromDatabase() throws Exception {

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("logout-user", "password"))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshCookie = extractCookie(loginResult, "refreshToken");
        assertThat(refreshCookie).isNotNull();

        assertThat(refreshTokenRepository.findByToken(refreshCookie.getValue())).isPresent();

        mockMvc.perform(post("/api/logout")
                .cookie(refreshCookie))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"))
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));

        assertThat(refreshTokenRepository.findByToken(refreshCookie.getValue())).isEmpty();
    }

    @Test
    @DisplayName("Deve realizar logout com sucesso mesmo sem cookie de refresh token")
    void logoutWithoutCookieShouldStillReturn200() throws Exception {
        mockMvc.perform(post("/api/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"))
                .andExpect(cookie().maxAge("accessToken", 0))
                .andExpect(cookie().maxAge("refreshToken", 0));
    }

    @Test
    @DisplayName("Após logout o refresh token não deve mais funcionar")
    void afterLogoutRefreshTokenShouldBeInvalid() throws Exception {

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("logout-user", "password"))))
                .andExpect(status().isOk())
                .andReturn();

        Cookie refreshCookie = extractCookie(loginResult, "refreshToken");
        assertThat(refreshCookie).isNotNull();

        mockMvc.perform(post("/api/logout")
                .cookie(refreshCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/refresh-token")
                .cookie(refreshCookie))
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