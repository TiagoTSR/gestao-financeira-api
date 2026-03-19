package br.com.decodex.gestaofinanceira.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.repository.RefreshTokenRepository;

@WebMvcTest(LogoutController.class)
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private JwtServiceGenerator jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final String BASE_URL = "/api/logout";

    @Test
    @WithMockUser
    @DisplayName("POST /logout - Deve retornar 200 e limpar cookies quando refresh token presente")
    void logoutWithRefreshTokenShouldClearCookiesAndDeleteToken() throws Exception {
        RefreshToken refreshToken = new RefreshToken();

        when(refreshTokenRepository.findByToken("token-valido"))
                .thenReturn(Optional.of(refreshToken));

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "token-valido")))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"))
                .andExpect(header().exists("Set-Cookie"));

        verify(refreshTokenRepository).findByToken("token-valido");
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /logout - Deve retornar 200 e limpar cookies mesmo sem refresh token")
    void logoutWithoutRefreshTokenShouldStillClearCookies() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"))
                .andExpect(header().exists("Set-Cookie"));

        verify(refreshTokenRepository, never()).findByToken(any());
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /logout - Deve retornar 200 quando refresh token não encontrado no banco")
    void logoutWithUnknownRefreshTokenShouldNotThrow() throws Exception {
        when(refreshTokenRepository.findByToken("token-desconhecido"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post(BASE_URL)
                .with(csrf())
                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "token-desconhecido")))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout realizado com sucesso"));

        verify(refreshTokenRepository).findByToken("token-desconhecido");
        verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
    }
}