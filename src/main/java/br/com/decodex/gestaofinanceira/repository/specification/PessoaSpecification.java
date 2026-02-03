package br.com.decodex.gestaofinanceira.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import jakarta.persistence.criteria.Predicate;

public class PessoaSpecification {

	public static Specification<Pessoa> filtrar(PessoaFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filter.getNome())) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("nome")),
                        "%" + filter.getNome().toLowerCase() + "%"
                    )
                );
            }

            if (filter.getAtivo() != null) {
                predicates.add(
                    cb.equal(root.get("ativo"), filter.getAtivo())
                );
            }

            if (filter.getEndereco() != null && StringUtils.hasText(filter.getEndereco().getCidade())) {
                predicates.add(
                    cb.like(
                        cb.lower(root.get("endereco").get("cidade")),
                        "%" + filter.getEndereco().getCidade().toLowerCase() + "%"
                    )
                );
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}