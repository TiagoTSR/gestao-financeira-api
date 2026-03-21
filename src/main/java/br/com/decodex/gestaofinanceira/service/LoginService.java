package br.com.decodex.gestaofinanceira.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.auth.AuthData;
import br.com.decodex.gestaofinanceira.dto.login.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.usuario.UsuarioResponse;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.UsuarioRepository;

@Service
public class LoginService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private RefreshTokenService refreshTokenService;
    @Autowired private JwtServiceGenerator jwtService;
    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private LoginRateLimitService rateLimitService; // ✅ novo

    @Transactional
    public AuthData autenticar(LoginRequest login, String dispositivo) {

        rateLimitService.verificar(login.username()); 
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login.username(), login.password())
            );
        } catch (Exception e) {
            rateLimitService.registrarFalha(login.username());
            throw e;
        }

        rateLimitService.registrarSucesso(login.username());

        Usuario usuario = usuarioRepository.findByUsername(login.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String accessToken = jwtService.generateToken(usuario);
        var refreshTokenEntity = refreshTokenService.createRefreshToken(usuario, dispositivo);

        return new AuthData(accessToken, refreshTokenEntity.getToken(), new UsuarioResponse(usuario));
    }
}