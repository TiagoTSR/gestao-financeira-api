package br.com.decodex.gestaofinanceira.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.decodex.gestaofinanceira.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
