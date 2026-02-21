package br.com.decodex.gestaofinanceira.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.LoginRequest;
import br.com.decodex.gestaofinanceira.dto.LoginResponse;
import br.com.decodex.gestaofinanceira.dto.UsuarioResponse;
import br.com.decodex.gestaofinanceira.model.Usuario;
import br.com.decodex.gestaofinanceira.repository.UsuarioRepository;

@Service
public class LoginService {

    @Autowired
    private UsuarioRepository repository; 
    @Autowired
    private JwtServiceGenerator jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public LoginResponse logar(LoginRequest login) {
 
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login.username(), 
                        login.password()
                )
        );

        Usuario user = repository.findByUsername(login.username())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, new UsuarioResponse(user));
    }
}