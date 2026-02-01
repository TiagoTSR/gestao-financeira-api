package br.com.decodex.gestaofinanceira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.decodex.gestaofinanceira.model.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
