package br.com.decodex.gestaofinanceira.exceptions;

public class TooManyRequestsException extends RuntimeException {
    
	private static final long serialVersionUID = 1L;
	private final long segundosRestantes;

    public TooManyRequestsException(long segundosRestantes) {
        super("Conta bloqueada por tentativas excessivas");
        this.segundosRestantes = segundosRestantes;
    }

    public long getSegundosRestantes() {
        return segundosRestantes;
    }
}