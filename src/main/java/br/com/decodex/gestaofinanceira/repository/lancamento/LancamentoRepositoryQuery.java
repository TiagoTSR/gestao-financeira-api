package br.com.decodex.gestaofinanceira.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoria;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaDia;

public interface LancamentoRepositoryQuery {
    
    List<LancamentoEstatisticaCategoria> porCategoria(LocalDate inicio, LocalDate fim);
    
    List<LancamentoEstatisticaDia> porDia(LocalDate inicio, LocalDate fim);
}