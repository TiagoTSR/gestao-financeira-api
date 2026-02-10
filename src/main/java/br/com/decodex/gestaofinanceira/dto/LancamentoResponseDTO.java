package br.com.decodex.gestaofinanceira.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.decodex.gestaofinanceira.model.TipoLancamento;

public record LancamentoResponseDTO(

        Long id,
        String descricao,
        LocalDate dataVencimento,
        LocalDate dataPagamento,
        BigDecimal valor,
        String observacao,
        TipoLancamento tipo,

        Long categoriaId,
        String categoriaNome,

        Long pessoaId,
        String pessoaNome
) {}
