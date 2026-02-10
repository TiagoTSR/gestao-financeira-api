package br.com.decodex.gestaofinanceira.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.dto.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.LancamentoMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.LancamentoRepository;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.specification.LancamentoSpecification;

@Service
public class LancamentoService {
	
	private final LancamentoRepository lancamentoRepository;
	private final PessoaRepository pessoaRepository;
    private final CategoriaRepository categoriaRepository;
    private final LancamentoMapper mapper;
	
	public LancamentoService(LancamentoRepository lancamentoRepository,
			PessoaRepository pessoaRepository,
            CategoriaRepository categoriaRepository,
            LancamentoMapper mapper) {
	     this.lancamentoRepository = lancamentoRepository;
	     this.pessoaRepository = pessoaRepository;
	     this.categoriaRepository = categoriaRepository;
	     this.mapper = mapper;
	}
	
	@Transactional(readOnly = true)
	public Page<LancamentoResponseDTO> findAll(LancamentoFilter filter, Pageable pageable) {
	    Specification<Lancamento> spec = LancamentoSpecification.filtrar(filter);
	    
	    return lancamentoRepository.findAll(spec, pageable).map(mapper::toDTO);
	}
	
    @Transactional(readOnly = true)
    public Lancamento findById(Long id) {

        return lancamentoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lançamento não encontrado para o ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public LancamentoResponseDTO findByIdDTO(Long id) {
        return mapper.toDTO(findById(id));
    }
    
    @Transactional
    public LancamentoResponseDTO create(LancamentoRequestDTO dto) {
    	
    	Pessoa pessoa = pessoaRepository.findById(dto.pessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada para o ID: " + dto.pessoaId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada para o ID: " + dto.categoriaId()));

        Lancamento lancamento = mapper.toEntity(dto, pessoa, categoria);
        Lancamento salvo = lancamentoRepository.save(lancamento);
        
        return mapper.toDTO(salvo);
    }
    
    @Transactional
    public LancamentoResponseDTO update(Long id, LancamentoRequestDTO dto) {

        Lancamento existente = findById(id);

        Pessoa pessoa = pessoaRepository.findById(dto.pessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada para o ID: " + dto.pessoaId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada para o ID: " + dto.categoriaId()));

        mapper.updateEntity(existente, dto);

        existente.setPessoa(pessoa);
        existente.setCategoria(categoria);

        Lancamento salvo = lancamentoRepository.save(existente);
        return mapper.toDTO(salvo);
    }



    public void delete(Long id) {
        lancamentoRepository.delete(findById(id));
    }
}