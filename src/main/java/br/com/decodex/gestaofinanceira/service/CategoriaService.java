package br.com.decodex.gestaofinanceira.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.CategoriaMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.CategoriaFilter;
import br.com.decodex.gestaofinanceira.repository.specification.CategoriaSpecification;

@Service
public class CategoriaService {
	
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper mapper;

    public CategoriaService(CategoriaRepository categoriaRepository,CategoriaMapper mapper){
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }
    
    @Transactional(readOnly = true)
    public Page<CategoriaResponseDTO> findAll(CategoriaFilter filter, Pageable pageable) {
	    Specification<Categoria> spec = CategoriaSpecification.filtrar(filter);
	    
	    return categoriaRepository.findAll(spec, pageable).map(mapper::toDTO);
	}

    @Transactional(readOnly = true)
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }
    
    @Transactional(readOnly = true)
    public CategoriaResponseDTO findByIdDTO(Long id) {
        return mapper.toDTO(findById(id));
    }

    public CategoriaResponseDTO create(CategoriaRequestDTO dto) {
    	Categoria categoria = mapper.toEntity(dto);
        Categoria salva = categoriaRepository.save(categoria);
        return mapper.toDTO(salva);
    }

    public CategoriaResponseDTO update(Long id, CategoriaRequestDTO dto) {
        Categoria existente = findById(id);
        mapper.updateEntity(existente, dto);
        Categoria salva = categoriaRepository.save(existente);
        return mapper.toDTO(salva);
    }

    public void delete(Long id) {
    	Categoria existente = findById(id);
        categoriaRepository.delete(existente);
    }
}