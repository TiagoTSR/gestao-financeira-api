package br.com.decodex.gestaofinanceira.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>,JpaSpecificationExecutor<Lancamento>,
                                                           LancamentoRepositoryQuery  {

}
