package br.com.decodex.gestaofinanceira.service;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

public final class QueryParamValidator {

    private static final Set<String> PAGINACAO_PERMITIDA = Set.of("page", "size", "sort");

    private QueryParamValidator() {
    	
    }

    public static void validate(HttpServletRequest request, Set<String> filtrosPermitidos) {

        for (String param : request.getParameterMap().keySet()) {
            if (!PAGINACAO_PERMITIDA.contains(param) && !filtrosPermitidos.contains(param)) {
                throw new IllegalArgumentException("Parâmetro inválido: " + param);
            }
        }
    }
}