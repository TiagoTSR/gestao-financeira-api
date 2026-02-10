package br.com.decodex.gestaofinanceira.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.filter.CategoriaFilter;
import jakarta.persistence.criteria.Predicate;

public class CategoriaSpecification {

    public static Specification<Categoria> filtrar(CategoriaFilter filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter != null && StringUtils.hasText(filter.getNome())) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("nome")),
                        "%" + filter.getNome().toLowerCase() + "%"
                    )
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}