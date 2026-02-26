package br.com.decodex.gestaofinanceira.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.model.Usuario;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    void deleteByUsuario(Usuario usuario);
}