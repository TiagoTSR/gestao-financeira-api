package br.com.decodex.gestaofinanceira.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.AuthData;
import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.UsuarioResponse;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.RefreshTokenRepository;
import br.com.decodex.gestaofinanceira.repository.UsuarioRepository;

@Service
public class LoginService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtServiceGenerator jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthData autenticar(LoginRequest login, String dispositivo) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(login.username(), login.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(login.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String accessToken = jwtService.generateToken(usuario);
        String refreshTokenString = jwtService.generateRefreshToken(usuario);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshTokenString);
        refreshTokenEntity.setDataExpiracao(jwtService.extractExpirationAsLocalDateTime(refreshTokenString));
        refreshTokenEntity.setUsuario(usuario);
        refreshTokenEntity.setDispositivo(dispositivo);
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthData(accessToken, refreshTokenString, new UsuarioResponse(usuario));
    }
}