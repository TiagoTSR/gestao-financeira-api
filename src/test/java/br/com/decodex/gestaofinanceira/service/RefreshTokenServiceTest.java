package br.com.decodex.gestaofinanceira.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.AuthData;
import br.com.decodex.gestaofinanceira.exceptions.InvalidTokenException;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.RefreshTokenRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtServiceGenerator jwtService;

    @InjectMocks
    private RefreshTokenService service;

    private Usuario usuario;
    private RefreshToken refreshToken;

    private static final String TOKEN_VALIDO = "refresh-token-valido";
    private static final String NOVO_ACCESS_TOKEN = "novo-access-token";
    private static final String NOVO_REFRESH_TOKEN = "novo-refresh-token";

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");
        usuario.setRole("ROLE_USER");

        refreshToken = new RefreshToken();
        refreshToken.setId(1L);
        refreshToken.setToken(TOKEN_VALIDO);
        refreshToken.setUsuario(usuario);
        refreshToken.setDataExpiracao(LocalDateTime.now().plusHours(1)); // não expirado
    }

    // -------------------------
    // renovarTokens
    // -------------------------

    @Test
    @DisplayName("Deve renovar tokens com sucesso quando refresh token é válido")
    void renovarTokensShouldReturnAuthDataWhenTokenIsValid() {
        when(refreshTokenRepository.findByTokenWithUsuario(TOKEN_VALIDO))
                .thenReturn(Optional.of(refreshToken));
        when(jwtService.isRefreshTokenValid(TOKEN_VALIDO)).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn(NOVO_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(usuario)).thenReturn(NOVO_REFRESH_TOKEN);
        when(jwtService.extractExpirationAsLocalDateTime(NOVO_REFRESH_TOKEN))
                .thenReturn(LocalDateTime.now().plusHours(24));
        when(refreshTokenRepository.save(any())).thenReturn(refreshToken);

        AuthData result = service.renovarTokens(TOKEN_VALIDO);

        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isEqualTo(NOVO_ACCESS_TOKEN);
        assertThat(result.refreshToken()).isEqualTo(NOVO_REFRESH_TOKEN);
        assertThat(result.usuario().username()).isEqualTo("joao");

        verify(refreshTokenRepository).save(refreshToken);
        assertThat(refreshToken.getToken()).isEqualTo(NOVO_REFRESH_TOKEN);
    }

    @Test
    @DisplayName("Deve lançar exceção quando refresh token não é encontrado no banco")
    void renovarTokensShouldThrowWhenTokenNotFound() {
        when(refreshTokenRepository.findByTokenWithUsuario(TOKEN_VALIDO))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.renovarTokens(TOKEN_VALIDO))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Refresh token não encontrado");

        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção e deletar token quando refresh token estiver expirado")
    void renovarTokensShouldThrowAndDeleteWhenTokenIsExpired() {
        refreshToken.setDataExpiracao(LocalDateTime.now().minusHours(1)); // expirado

        when(refreshTokenRepository.findByTokenWithUsuario(TOKEN_VALIDO))
                .thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> service.renovarTokens(TOKEN_VALIDO))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Refresh token expirado ou inválido");

        verify(refreshTokenRepository).delete(refreshToken);
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando token JWT interno for inválido mas data ainda não expirou")
    void renovarTokensShouldThrowWhenJwtIsInvalidButDateIsValid() {
        when(refreshTokenRepository.findByTokenWithUsuario(TOKEN_VALIDO))
                .thenReturn(Optional.of(refreshToken));
        when(jwtService.isRefreshTokenValid(TOKEN_VALIDO)).thenReturn(false); // JWT inválido

        assertThatThrownBy(() -> service.renovarTokens(TOKEN_VALIDO))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Refresh token expirado ou inválido");

        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    @DisplayName("Deve lançar exceção quando token não tiver usuário associado")
    void renovarTokensShouldThrowWhenTokenHasNoUsuario() {
        refreshToken.setUsuario(null); // token órfão

        when(refreshTokenRepository.findByTokenWithUsuario(TOKEN_VALIDO))
                .thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> service.renovarTokens(TOKEN_VALIDO))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Token órfão");

        verify(refreshTokenRepository, never()).save(any());
    }

    // -------------------------
    // createRefreshToken
    // -------------------------

    @Test
    @DisplayName("Deve criar refresh token com sucesso")
    void createRefreshTokenShouldSaveAndReturnToken() {
        String dispositivo = "Mozilla/5.0";

        when(jwtService.generateRefreshToken(usuario)).thenReturn(NOVO_REFRESH_TOKEN);
        when(jwtService.extractExpirationAsLocalDateTime(NOVO_REFRESH_TOKEN))
                .thenReturn(LocalDateTime.now().plusHours(24));
        when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        RefreshToken result = service.createRefreshToken(usuario, dispositivo);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo(NOVO_REFRESH_TOKEN);
        assertThat(result.getUsuario()).isEqualTo(usuario);
        assertThat(result.getDispositivo()).isEqualTo(dispositivo);
        assertThat(result.getDataExpiracao()).isAfter(LocalDateTime.now());

        verify(refreshTokenRepository).deleteByUsuarioAndDispositivo(usuario, dispositivo);
        verify(refreshTokenRepository).save(any());
    }

    @Test
    @DisplayName("Deve deletar token anterior do mesmo dispositivo antes de criar novo")
    void createRefreshTokenShouldDeleteOldTokenBeforeSaving() {
        String dispositivo = "Chrome/120";

        when(jwtService.generateRefreshToken(usuario)).thenReturn(NOVO_REFRESH_TOKEN);
        when(jwtService.extractExpirationAsLocalDateTime(NOVO_REFRESH_TOKEN))
                .thenReturn(LocalDateTime.now().plusHours(24));
        when(refreshTokenRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.createRefreshToken(usuario, dispositivo);

        var inOrder = org.mockito.Mockito.inOrder(refreshTokenRepository);
        inOrder.verify(refreshTokenRepository).deleteByUsuarioAndDispositivo(usuario, dispositivo);
        inOrder.verify(refreshTokenRepository).save(any());
    }
}