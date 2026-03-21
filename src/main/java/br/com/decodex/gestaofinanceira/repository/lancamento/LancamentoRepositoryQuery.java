package br.com.decodex.gestaofinanceira.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoria;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaDia;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.projection.ResumoLancamento;

public interface LancamentoRepositoryQuery {
    
    List<LancamentoEstatisticaCategoria> porCategoria(LocalDate inicio, LocalDate fim);
    
    List<LancamentoEstatisticaDia> porDia(LocalDate inicio, LocalDate fim);
    
    Page<ResumoLancamento> resumir(LancamentoFilter filtro, Pageable pageable);
}