package br.com.decodex.gestaofinanceira.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import br.com.decodex.gestaofinanceira.model.Contato;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class PessoaSpecification {

	public static Specification<Pessoa> filtrar(PessoaFilter filter) {
	    return (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();

	        if (Long.class != query.getResultType()) {
	            root.fetch("contatos", JoinType.LEFT);
	        }

	        if (StringUtils.hasText(filter.getNome())) {
	            predicates.add(cb.like(cb.lower(root.get("nome")),
	                "%" + filter.getNome().toLowerCase() + "%"));
	        }

	        if (filter.getAtivo() != null) {
	            predicates.add(cb.equal(root.get("ativo"), filter.getAtivo()));
	        }

	        if (filter.getEndereco() != null && StringUtils.hasText(filter.getEndereco().getCidade())) {
	            predicates.add(cb.like(cb.lower(root.get("endereco").get("cidade")),
	                "%" + filter.getEndereco().getCidade().toLowerCase() + "%"));
	        }

	        boolean temFiltroContato = StringUtils.hasText(filter.getContatoNome())
	            || StringUtils.hasText(filter.getContatoEmail())
	            || StringUtils.hasText(filter.getContatoTelefone());

	        if (temFiltroContato) {
	            Join<Pessoa, Contato> contatoJoin = root.join("contatos", JoinType.LEFT);

	            if (StringUtils.hasText(filter.getContatoNome())) {
	                predicates.add(cb.like(cb.lower(contatoJoin.get("nome")),
	                    "%" + filter.getContatoNome().toLowerCase() + "%"));
	            }
	            if (StringUtils.hasText(filter.getContatoEmail())) {
	                predicates.add(cb.equal(cb.lower(contatoJoin.get("email")),
	                    filter.getContatoEmail().toLowerCase()));
	            }
	            if (StringUtils.hasText(filter.getContatoTelefone())) {
	                predicates.add(cb.equal(contatoJoin.get("telefone"),
	                    filter.getContatoTelefone()));
	            }
	        }

	        query.distinct(true); 
	        return cb.and(predicates.toArray(new Predicate[0]));
	    };
	}
}