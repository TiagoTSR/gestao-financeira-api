package br.com.decodex.gestaofinanceira.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.CategoriaMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper mapper;

    public CategoriaService(CategoriaRepository categoriaRepository,CategoriaMapper mapper){
        this.categoriaRepository = categoriaRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> findAll() {
    	return categoriaRepository.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
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