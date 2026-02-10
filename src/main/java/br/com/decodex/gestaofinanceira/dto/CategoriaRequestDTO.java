package br.com.decodex.gestaofinanceira.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

        @NotBlank
        @Size(min = 3, max = 50)
        String nome
) {}
