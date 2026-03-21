package br.com.decodex.gestaofinanceira.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoria;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaDia;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaCategoria> query = 
            cb.createQuery(LancamentoEstatisticaCategoria.class);
        
        Root<Lancamento> root = query.from(Lancamento.class);
        
        query.select(cb.construct(
            LancamentoEstatisticaCategoria.class,
            root.get("categoria"),
            cb.sum(root.get("valor"))
        ));
        
        Predicate[] predicates = criarRestricoes(root, cb, inicio, fim);
        query.where(predicates);
        
        query.groupBy(root.get("categoria"));
        
        TypedQuery<LancamentoEstatisticaCategoria> typedQuery = manager.createQuery(query);
        return typedQuery.getResultList();
    }

    @Override
    public List<LancamentoEstatisticaDia> porDia(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaDia> query = 
            cb.createQuery(LancamentoEstatisticaDia.class);
        
        Root<Lancamento> root = query.from(Lancamento.class);
        
        query.select(cb.construct(
            LancamentoEstatisticaDia.class,
            root.get("tipo"),
            root.get("dataVencimento"),
            cb.sum(root.get("valor"))
        ));
        
        Predicate[] predicates = criarRestricoes(root, cb, inicio, fim);
        query.where(predicates);
        
        query.groupBy(
            root.get("tipo"),
            root.get("dataVencimento")
        );
        
        TypedQuery<LancamentoEstatisticaDia> typedQuery = manager.createQuery(query);
        return typedQuery.getResultList();
    }
    
    private Predicate[] criarRestricoes(Root<Lancamento> root, 
                                        CriteriaBuilder cb, 
                                        LocalDate inicio, 
                                        LocalDate fim) {
        return new Predicate[] {
            cb.greaterThanOrEqualTo(root.get("dataVencimento"), inicio),
            cb.lessThanOrEqualTo(root.get("dataVencimento"), fim)
        };
    }
}