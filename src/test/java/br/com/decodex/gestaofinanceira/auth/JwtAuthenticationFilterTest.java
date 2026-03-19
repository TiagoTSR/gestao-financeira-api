package br.com.decodex.gestaofinanceira.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import jakarta.servlet.http.Cookie;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtServiceGenerator jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    private static final String TOKEN_VALIDO = "token.valido.jwt";

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        SecurityContextHolder.clearContext();
    }

    // -------------------------
    // Rotas públicas
    // -------------------------

    @Test
    @DisplayName("Deve passar direto para /api/login sem processar token")
    void shouldSkipFilterForLoginPath() throws Exception {
        request.setServletPath("/api/login");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
        verify(jwtService, never()).extractUsername(TOKEN_VALIDO);
    }

    @Test
    @DisplayName("Deve passar direto para /api/register sem processar token")
    void shouldSkipFilterForRegisterPath() throws Exception {
        request.setServletPath("/api/register");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
        verify(jwtService, never()).extractUsername(TOKEN_VALIDO);
    }

    @Test
    @DisplayName("Deve passar direto para /api/refresh-token sem processar token")
    void shouldSkipFilterForRefreshTokenPath() throws Exception {
        request.setServletPath("/api/refresh-token");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
        verify(jwtService, never()).extractUsername(TOKEN_VALIDO);
    }

    // -------------------------
    // Sem token
    // -------------------------

    @Test
    @DisplayName("Deve passar sem autenticar quando não há token")
    void shouldPassWithoutAuthWhenNoToken() throws Exception {
        request.setServletPath("/api/lancamentos");

        filter.doFilterInternal(request, response, filterChain);

        assertThat(filterChain.getRequest()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // -------------------------
    // Token via Authorization header
    // -------------------------

    @Test
    @DisplayName("Deve autenticar quando token válido está no header Authorization")
    void shouldAuthenticateWhenValidTokenInHeader() throws Exception {
        request.setServletPath("/api/lancamentos");
        request.addHeader("Authorization", "Bearer " + TOKEN_VALIDO);

        UserDetails userDetails = User.withUsername("joao")
                .password("senha")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(jwtService.extractUsername(TOKEN_VALIDO)).thenReturn("joao");
        when(userDetailsService.loadUserByUsername("joao")).thenReturn(userDetails);
        when(jwtService.isTokenValid(TOKEN_VALIDO, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("joao");
        assertThat(filterChain.getRequest()).isNotNull();
    }

    // -------------------------
    // Token via Cookie
    // -------------------------

    @Test
    @DisplayName("Deve autenticar quando token válido está no cookie accessToken")
    void shouldAuthenticateWhenValidTokenInCookie() throws Exception {
        request.setServletPath("/api/lancamentos");
        request.setCookies(new Cookie("accessToken", TOKEN_VALIDO));

        UserDetails userDetails = User.withUsername("joao")
                .password("senha")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(jwtService.extractUsername(TOKEN_VALIDO)).thenReturn("joao");
        when(userDetailsService.loadUserByUsername("joao")).thenReturn(userDetails);
        when(jwtService.isTokenValid(TOKEN_VALIDO, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("joao");
    }

    @Test
    @DisplayName("Deve limpar caracteres especiais do token no cookie")
    void shouldCleanSpecialCharsFromCookieToken() throws Exception {
        String tokenComCaracteres = "\"[" + TOKEN_VALIDO + "]\"";
        request.setServletPath("/api/lancamentos");
        request.setCookies(new Cookie("accessToken", tokenComCaracteres));

        UserDetails userDetails = User.withUsername("joao")
                .password("senha")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(jwtService.extractUsername(TOKEN_VALIDO)).thenReturn("joao");
        when(userDetailsService.loadUserByUsername("joao")).thenReturn(userDetails);
        when(jwtService.isTokenValid(TOKEN_VALIDO, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    // -------------------------
    // Token inválido
    // -------------------------

    @Test
    @DisplayName("Deve passar sem autenticar quando isTokenValid retorna false")
    void shouldNotAuthenticateWhenTokenIsInvalid() throws Exception {
        request.setServletPath("/api/lancamentos");
        request.addHeader("Authorization", "Bearer " + TOKEN_VALIDO);

        UserDetails userDetails = User.withUsername("joao")
                .password("senha")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        when(jwtService.extractUsername(TOKEN_VALIDO)).thenReturn("joao");
        when(userDetailsService.loadUserByUsername("joao")).thenReturn(userDetails);
        when(jwtService.isTokenValid(TOKEN_VALIDO, userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(filterChain.getRequest()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar 401 quando extractUsername lança exceção")
    void shouldReturn401WhenExtractUsernameThrowsException() throws Exception {
        request.setServletPath("/api/lancamentos");
        request.addHeader("Authorization", "Bearer " + TOKEN_VALIDO);

        when(jwtService.extractUsername(TOKEN_VALIDO))
                .thenThrow(new RuntimeException("Token corrompido"));

        filter.doFilterInternal(request, response, filterChain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Token inválido ou expirado");
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // -------------------------
    // Contexto já autenticado
    // -------------------------

    @Test
    @DisplayName("Não deve reautenticar quando contexto já tem autenticação")
    void shouldNotReauthenticateWhenAlreadyAuthenticated() throws Exception {
        request.setServletPath("/api/lancamentos");
        request.addHeader("Authorization", "Bearer " + TOKEN_VALIDO);

        UserDetails userDetails = User.withUsername("joao")
                .password("senha")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();

        org.springframework.security.core.Authentication existingAuth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(jwtService.extractUsername(TOKEN_VALIDO)).thenReturn("joao");

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserByUsername("joao");
        assertThat(filterChain.getRequest()).isNotNull();
    }
}