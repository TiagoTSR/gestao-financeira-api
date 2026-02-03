package br.com.decodex.gestaofinanceira.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.decodex.gestaofinanceira.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>,JpaSpecificationExecutor<Pessoa> {

}
