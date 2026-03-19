package br.com.decodex.gestaofinanceira.auth;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.decodex.gestaofinanceira.config.property.GestaoApiProperty;
import br.com.decodex.gestaofinanceira.model.Usuario;

class JwtServiceGeneratorTest {

    private JwtServiceGenerator jwtService;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
   
        GestaoApiProperty property = new GestaoApiProperty();
        property.getJwt().setSecret("chave-secreta-super-longa-para-testes-com-512-bits-1234567890");
        property.getJwt().setAccessExpirationMinutes(60L);  // ✅ 60 minutos = 1 hora
        property.getJwt().setAccessExpirationHours(24L); // 24 horas

        jwtService = new JwtServiceGenerator(property);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("joao");
        usuario.setRole("ROLE_USER");
    }

    // -------------------------
    // generateToken / extractUsername
    // -------------------------

    @Test
    @DisplayName("Deve gerar access token e extrair username corretamente")
    void generateTokenShouldContainCorrectUsername() {
        String token = jwtService.generateToken(usuario);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtService.extractUsername(token)).isEqualTo("joao");
    }

    @Test
    @DisplayName("Deve gerar refresh token e extrair username corretamente")
    void generateRefreshTokenShouldContainCorrectUsername() {
        String token = jwtService.generateRefreshToken(usuario);

        assertThat(token).isNotNull().isNotEmpty();
        assertThat(jwtService.extractUsername(token)).isEqualTo("joao");
    }

    @Test
    @DisplayName("Access token e refresh token devem ser diferentes")
    void accessAndRefreshTokensShouldBeDifferent() {
        String accessToken = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        assertThat(accessToken).isNotEqualTo(refreshToken);
    }

    // -------------------------
    // extractExpiration
    // -------------------------

    @Test
    @DisplayName("Deve extrair data de expiração futura do access token")
    void extractExpirationShouldReturnFutureDate() {
        String token = jwtService.generateToken(usuario);
        Date expiration = jwtService.extractExpiration(token);

        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Deve extrair expiração como LocalDateTime")
    void extractExpirationAsLocalDateTimeShouldReturnFutureDateTime() {
        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.extractExpirationAsLocalDateTime(token))
                .isAfter(java.time.LocalDateTime.now());
    }

    // -------------------------
    // isTokenValid
    // -------------------------

    @Test
    @DisplayName("Deve validar access token com UserDetails correto")
    void isTokenValidShouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(usuario);
        UserDetails userDetails = User.withUsername("joao")
                .password("senha")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false para token com username diferente")
    void isTokenValidShouldReturnFalseForWrongUsername() {
        String token = jwtService.generateToken(usuario);
        UserDetails outroUser = User.withUsername("outro-usuario")
                .password("senha")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(token, outroUser)).isFalse();
    }

    @Test
    @DisplayName("Deve validar token sem UserDetails")
    void isTokenValidWithoutUserDetailsShouldReturnTrue() {
        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false para token inválido sem UserDetails")
    void isTokenValidWithoutUserDetailsShouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isTokenValid("token.invalido.qualquer")).isFalse();
    }

    // -------------------------
    // isRefreshTokenValid
    // -------------------------

    @Test
    @DisplayName("Deve validar refresh token com type correto")
    void isRefreshTokenValidShouldReturnTrueForRefreshToken() {
        String token = jwtService.generateRefreshToken(usuario);

        assertThat(jwtService.isRefreshTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("Deve retornar false para access token no isRefreshTokenValid")
    void isRefreshTokenValidShouldReturnFalseForAccessToken() {
        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.isRefreshTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar false para token malformado no isRefreshTokenValid")
    void isRefreshTokenValidShouldReturnFalseForInvalidToken() {
        assertThat(jwtService.isRefreshTokenValid("token.invalido")).isFalse();
    }
    
    
}