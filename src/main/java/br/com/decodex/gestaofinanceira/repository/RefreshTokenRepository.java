package br.com.decodex.gestaofinanceira.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.model.RefreshToken;
import br.com.decodex.gestaofinanceira.model.Usuario;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Query("SELECT rt FROM RefreshToken rt JOIN FETCH rt.usuario WHERE rt.token = :token")
    Optional<RefreshToken> findByTokenWithUsuario(@Param("token") String token);
   
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.usuario.id = :usuarioId")
    void deleteAllByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.usuario = :usuario AND rt.dispositivo = :dispositivo")
    void deleteByUsuarioAndDispositivo(@Param("usuario") Usuario usuario, @Param("dispositivo") String dispositivo);

  
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.dataExpiracao < CURRENT_TIMESTAMP")
    void deleteAllExpiredTokens();
}