package br.com.decodex.gestaofinanceira.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class LancamentoSpecification {

    private LancamentoSpecification() {
    }

    public static Specification<Lancamento> filtrar(LancamentoFilter filter) {
        return (root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (filter == null) {
                return builder.conjunction();
            }

            if (temTexto(filter.getDescricao())) {
                String like = "%" + filter.getDescricao().trim().toLowerCase() + "%";
                predicates.add(builder.like(
                        builder.lower(root.get("descricao")),
                        like
                ));
            }

            if (filter.getDataVencimentoDe() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get("dataVencimento"),
                        filter.getDataVencimentoDe()
                ));
            }

            if (filter.getDataVencimentoAte() != null) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get("dataVencimento"),
                        filter.getDataVencimentoAte()
                ));
            }

            if (filter.getValorMin() != null) {
                predicates.add(builder.greaterThanOrEqualTo(
                        root.get("valor"),
                        filter.getValorMin()
                ));
            }

            if (filter.getValorMax() != null) {
                predicates.add(builder.lessThanOrEqualTo(
                        root.get("valor"),
                        filter.getValorMax()
                ));
            }

            if (filter.getPessoaId() != null) {
                Join<Object, Object> pessoaJoin = root.join("pessoa", JoinType.INNER);
                predicates.add(builder.equal(
                        pessoaJoin.get("id"),
                        filter.getPessoaId()
                ));
            }

            if (filter.getCategoriaId() != null) {
                Join<Object, Object> categoriaJoin = root.join("categoria", JoinType.INNER);
                predicates.add(builder.equal(
                        categoriaJoin.get("id"),
                        filter.getCategoriaId()
                ));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean temTexto(String value) {
        return value != null && !value.trim().isEmpty();
    }
}