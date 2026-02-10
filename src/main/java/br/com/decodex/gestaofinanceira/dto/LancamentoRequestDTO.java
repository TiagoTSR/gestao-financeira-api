package br.com.decodex.gestaofinanceira.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.decodex.gestaofinanceira.model.TipoLancamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LancamentoRequestDTO(

        @NotBlank
        String descricao,

        @NotNull
        LocalDate dataVencimento,

        LocalDate dataPagamento,

        @NotNull
        BigDecimal valor,

        String observacao,

        @NotNull
        TipoLancamento tipo,

        @NotNull
        Long categoriaId,

        @NotNull
        Long pessoaId
) {}
