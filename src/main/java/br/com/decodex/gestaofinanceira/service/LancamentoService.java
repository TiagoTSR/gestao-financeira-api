package br.com.decodex.gestaofinanceira.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.repository.LancamentoRepository;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.specification.LancamentoSpecification;

@Service
public class LancamentoService {
	
	private final LancamentoRepository lancamentoRepository;
	
	public LancamentoService(LancamentoRepository lancamentoRepository) {
	     this.lancamentoRepository = lancamentoRepository;	
	}
	
	@Transactional(readOnly = true)
	public Page<Lancamento> findAll(LancamentoFilter filter, Pageable pageable) {
	    Specification<Lancamento> spec = LancamentoSpecification.filtrar(filter);
	    return lancamentoRepository.findAll(spec, pageable);
	}
	
    @Transactional(readOnly = true)
    public Lancamento findById(Long id) {

        return lancamentoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lançamento não encontrado para o ID: " + id));
    }

    public Lancamento create(Lancamento lancamento) {

        lancamento.setId(null);
        return lancamentoRepository.save(lancamento);
    }

    public Lancamento update(Long id, Lancamento lancamento) {
 
        Lancamento existente = findById(id);

        existente.setDescricao(lancamento.getDescricao());
        existente.setDataVencimento(lancamento.getDataVencimento());
        existente.setDataPagamento(lancamento.getDataPagamento());
        existente.setValor(lancamento.getValor());
        existente.setPessoa(lancamento.getPessoa());
        existente.setCategoria(lancamento.getCategoria());

        return lancamentoRepository.save(existente);
    }

    public void delete(Long id) {
        lancamentoRepository.delete(findById(id));
    }
}