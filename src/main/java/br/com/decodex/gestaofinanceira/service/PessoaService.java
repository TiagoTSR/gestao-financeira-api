package br.com.decodex.gestaofinanceira.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.PessoaMapper;
import br.com.decodex.gestaofinanceira.model.Endereco;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import br.com.decodex.gestaofinanceira.repository.specification.PessoaSpecification;

@Service
public class PessoaService {
	
	private final PessoaRepository pessoaRepository;
	private final PessoaMapper mapper;
	
	public PessoaService(PessoaRepository pessoaRepository,PessoaMapper mapper) {
		this.pessoaRepository = pessoaRepository;
		this.mapper = mapper;
	}
	
	@Transactional(readOnly = true)
	public Page<PessoaResponseDTO> findAll(PessoaFilter filter, Pageable pageable) {
	    return pessoaRepository.findAll(
	            PessoaSpecification.filtrar(filter),
	            pageable).map(mapper::toDTO);
	}

	@Transactional(readOnly = true)
	public Pessoa findById(Long id) {
	    	
	    return pessoaRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));
	}
	
	@Transactional(readOnly = true)
    public PessoaResponseDTO findByIdDTO(Long id) {
        return mapper.toDTO(findById(id));
    }

	@Transactional
	public PessoaResponseDTO create(PessoaRequestDTO dto) {
		
		Pessoa pessoa = mapper.toEntity(dto);
        
        if (pessoa.getAtivo() == null) {
            pessoa.setAtivo(true);
        }
        
        Pessoa salva = pessoaRepository.save(pessoa);
	    return mapper.toDTO(salva);
 
	}

	@Transactional
	public PessoaResponseDTO update(Long id, PessoaRequestDTO dto) {
		Pessoa existente = findById(id);

        mapper.updateEntity(existente, dto);

	    Endereco endereco = existente.getEndereco();
	    if (endereco != null) {
	    	existente.setEndereco(endereco);
	 }
	    Pessoa salva = pessoaRepository.save(existente);
	    return mapper.toDTO(salva);
	 }

	 @Transactional
	 public void delete(Long id) {
	    Pessoa entity = pessoaRepository.findById(id)
	     .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada"));

	    pessoaRepository.delete(entity);
	 }
}