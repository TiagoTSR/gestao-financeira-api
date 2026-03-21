package br.com.decodex.gestaofinanceira.service;

import br.com.decodex.gestaofinanceira.exceptions.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimitService {

    private static final int MAX_TENTATIVAS = 5;
    private static final long BLOQUEIO_SEGUNDOS = 300; // 5 minutos

    private record DadosTentativa(int tentativas, Instant bloqueadoAte) {}

    private final ConcurrentHashMap<String, DadosTentativa> tentativas = new ConcurrentHashMap<>();

    public void verificar(String username) {
        DadosTentativa dados = tentativas.get(username);

        if (dados != null && dados.bloqueadoAte() != null) {
            long segundosRestantes = Instant.now().until(dados.bloqueadoAte(), java.time.temporal.ChronoUnit.SECONDS);
            if (segundosRestantes > 0) {
                throw new TooManyRequestsException(segundosRestantes);
            }
            // bloqueio expirou, limpa
            tentativas.remove(username);
        }
    }

    public void registrarFalha(String username) {
        DadosTentativa atual = tentativas.getOrDefault(username, new DadosTentativa(0, null));
        int novasTentativas = atual.tentativas() + 1;

        if (novasTentativas >= MAX_TENTATIVAS) {
            tentativas.put(username, new DadosTentativa(novasTentativas, Instant.now().plusSeconds(BLOQUEIO_SEGUNDOS)));
        } else {
            tentativas.put(username, new DadosTentativa(novasTentativas, null));
        }
    }

    public void registrarSucesso(String username) {
        tentativas.remove(username);
    }
}