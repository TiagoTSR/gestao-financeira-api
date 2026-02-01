package br.com.decodex.gestaofinanceira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.decodex.gestaofinanceira.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

}
