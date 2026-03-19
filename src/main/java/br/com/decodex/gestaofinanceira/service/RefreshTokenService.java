package br.com.decodex.gestaofinanceira.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.AuthData;
import br.com.decodex.gestaofinanceira.dto.UsuarioResponse;
import br.com.decodex.gestaofinanceira.exceptions.InvalidTokenException;
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
    public AuthData renovarTokens(String tokenValue) {
        // 1. Busca e valida existência
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenWithUsuario(tokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token não encontrado"));
        
        if (refreshTokenEntity.getUsuario() == null) {
            throw new InvalidTokenException("Token órfão: nenhum usuário associado");
        }

        // 2. Verifica expiração e integridade do JWT
        if (refreshTokenEntity.getDataExpiracao().isBefore(LocalDateTime.now()) || 
            !jwtService.isRefreshTokenValid(tokenValue)) {
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new InvalidTokenException("Refresh token expirado ou inválido");
        }

        Usuario usuario = refreshTokenEntity.getUsuario();

        // 3. Gera novos tokens (Refresh Token Rotation)
        String newAccessToken = jwtService.generateToken(usuario);
        String newRefreshToken = jwtService.generateRefreshToken(usuario);

        // 4. Atualiza a entidade existente
        refreshTokenEntity.setToken(newRefreshToken);
        refreshTokenEntity.setDataExpiracao(jwtService.extractExpirationAsLocalDateTime(newRefreshToken));
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthData(newAccessToken, newRefreshToken, new UsuarioResponse(usuario));
    }

    @Transactional
    public RefreshToken createRefreshToken(Usuario usuario, String dispositivo) {
        refreshTokenRepository.deleteByUsuarioAndDispositivo(usuario, dispositivo);
        
        String tokenJwt = jwtService.generateRefreshToken(usuario);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenJwt);
        refreshToken.setDataExpiracao(jwtService.extractExpirationAsLocalDateTime(tokenJwt));
        refreshToken.setUsuario(usuario);
        refreshToken.setDispositivo(dispositivo);
        
        return refreshTokenRepository.save(refreshToken);
    }
}