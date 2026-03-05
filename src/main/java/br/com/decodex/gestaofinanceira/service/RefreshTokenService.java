package br.com.decodex.gestaofinanceira.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtServiceGenerator jwtService;

    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario, String dispositivo) {
        refreshTokenRepository.deleteByUsuarioAndDispositivo(usuario, dispositivo);

        String tokenJwt = jwtService.generateRefreshToken(usuario);
        LocalDateTime expiracao = jwtService.extractExpirationAsLocalDateTime(tokenJwt);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenJwt);
        refreshToken.setDataExpiracao(expiracao);
        refreshToken.setUsuario(usuario);
        refreshToken.setDispositivo(dispositivo);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByTokenWithUsuario(String token) {
        return refreshTokenRepository.findByTokenWithUsuario(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getDataExpiracao().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Faça login novamente.");
        }
        return token;
    }
    
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void delete(RefreshToken token) {
        refreshTokenRepository.delete(token);
    }

    @Transactional
    public void deleteAllByUsuario(Usuario usuario) {
        refreshTokenRepository.deleteAllByUsuarioId(usuario.getId());
    }
}