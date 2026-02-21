package br.com.decodex.gestaofinanceira.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank String username, 
    @NotBlank String password
) {}