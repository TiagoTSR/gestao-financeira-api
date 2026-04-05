package br.com.decodex.gestaofinanceira.repository.lancamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoria;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoriaQuantidade;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaDia;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaPessoa;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.projection.ResumoLancamento;
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
    
    public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaPessoa> query =
            cb.createQuery(LancamentoEstatisticaPessoa.class);

        Root<Lancamento> root = query.from(Lancamento.class);

        query.select(cb.construct(
            LancamentoEstatisticaPessoa.class,
            root.get("tipo"),
            root.get("pessoa"),
            cb.sum(root.get("valor"))
        ));

        Predicate[] predicates = criarRestricoesPorData(root, cb, inicio, fim);
        query.where(predicates);

        query.groupBy(
            root.get("tipo"),
            root.get("pessoa")
        );

        TypedQuery<LancamentoEstatisticaPessoa> typedQuery =
            manager.createQuery(query);

        return typedQuery.getResultList();
        }


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
    
    public List<LancamentoEstatisticaCategoriaQuantidade> porCategoriaQuantidade(LocalDate inicio, LocalDate fim) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaCategoriaQuantidade> query =
            cb.createQuery(LancamentoEstatisticaCategoriaQuantidade.class);

        Root<Lancamento> root = query.from(Lancamento.class);

        query.select(cb.construct(
            LancamentoEstatisticaCategoriaQuantidade.class,
            root.get("categoria"),
            cb.count(root)
        ));

        Predicate[] predicates = criarRestricoes(root, cb, inicio, fim);
        query.where(predicates);

        query.groupBy(root.get("categoria"));

        return manager.createQuery(query).getResultList();
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
    
    @Override
    public Page<ResumoLancamento> resumir(LancamentoFilter filtro, Pageable pageable) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<ResumoLancamento> query = cb.createQuery(ResumoLancamento.class);
        Root<Lancamento> root = query.from(Lancamento.class);
        
        query.select(cb.construct(
            ResumoLancamento.class,
            root.get("id"),
            root.get("descricao"),
            root.get("dataVencimento"),
            root.get("dataPagamento"),
            root.get("valor"),
            root.get("tipo"),
            root.get("categoria").get("nome"),
            root.get("pessoa").get("nome")
        ));
        
        Predicate[] predicates = criarRestricoesPorFiltro(filtro, cb, root);
        query.where(predicates);
        
        TypedQuery<ResumoLancamento> typedQuery = manager.createQuery(query);
        adicionarRestricoesDePaginacao(typedQuery, pageable);
        
        return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
    }
    
    private Predicate[] criarRestricoesPorData(Root<Lancamento> root, 
                                               CriteriaBuilder cb, 
                                               LocalDate inicio, 
                                               LocalDate fim) {
        return new Predicate[] {
            cb.greaterThanOrEqualTo(root.get("dataVencimento"), inicio),
            cb.lessThanOrEqualTo(root.get("dataVencimento"), fim)
        };
    }

    private Predicate[] criarRestricoesPorFiltro(LancamentoFilter filtro, 
                                                  CriteriaBuilder cb, 
                                                  Root<Lancamento> root) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (StringUtils.hasText(filtro.getDescricao())) {
            predicates.add(cb.like(
                cb.lower(root.get("descricao")), 
                "%" + filtro.getDescricao().toLowerCase() + "%"
            ));
        }
        
        if (filtro.getDataVencimentoDe() != null) {
            predicates.add(
                cb.greaterThanOrEqualTo(root.get("dataVencimento"), filtro.getDataVencimentoDe())
            );
        }
        
        if (filtro.getDataVencimentoAte() != null) {
            predicates.add(
                cb.lessThanOrEqualTo(root.get("dataVencimento"), filtro.getDataVencimentoAte())
            );
        }
        
        return predicates.toArray(new Predicate[0]);
    }

    private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPorPagina = pageable.getPageSize();
        int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
        
        query.setFirstResult(primeiroRegistroDaPagina);
        query.setMaxResults(totalRegistrosPorPagina);
    }

    private Long total(LancamentoFilter filtro) {
        CriteriaBuilder cb = manager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Lancamento> root = query.from(Lancamento.class);
        
        Predicate[] predicates = criarRestricoesPorFiltro(filtro, cb, root);
        query.where(predicates);
        
        query.select(cb.count(root));
        return manager.createQuery(query).getSingleResult();
    }
}