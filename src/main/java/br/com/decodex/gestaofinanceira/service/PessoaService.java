package br.com.decodex.gestaofinanceira.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.model.Endereco;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import br.com.decodex.gestaofinanceira.repository.specification.PessoaSpecification;

@Service
public class PessoaService {
	
	private final PessoaRepository pessoaRepository;
	
	public PessoaService(PessoaRepository pessoaRepository) {
		this.pessoaRepository = pessoaRepository;
	}
	
	@Transactional(readOnly = true)
	public Page<Pessoa> findAll(PessoaFilter filter, Pageable pageable) {
	    return pessoaRepository.findAll(
	            PessoaSpecification.filtrar(filter),
	            pageable
	    );
	}

	@Transactional(readOnly = true)
	public Pessoa findById(Long id) {
	    	
	    return pessoaRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));
	}

	@Transactional
	public Pessoa create(Pessoa pessoa) {

	    return pessoaRepository.save(pessoa);
	}

	@Transactional
	public Pessoa update(Pessoa pessoa) {
	    Pessoa entity = pessoaRepository.findById(pessoa.getId())
	     .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));

	    entity.setNome(pessoa.getNome());

	    Endereco endereco = pessoa.getEndereco();
	    if (endereco != null) {
	        entity.setEndereco(endereco);
	 }

	    return pessoaRepository.save(entity);
	 }

	 @Transactional
	 public void delete(Long id) {
	    Pessoa entity = pessoaRepository.findById(id)
	     .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));

	    pessoaRepository.delete(entity);
	 }
}