package br.com.decodex.gestaofinanceira.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;

@Service
public class CategoriaService {
	
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria findById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    public Categoria create(Categoria categoria) {
        categoria.setId(null); 
        return categoriaRepository.save(categoria);
    }

    public Categoria update(Long id, Categoria categoriaRequest) {
        Categoria existente = findById(id);
        existente.setNome(categoriaRequest.getNome());
        return categoriaRepository.save(existente);
    }

    public void delete(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("ID inexistente");
        }
        categoriaRepository.deleteById(id);
    }
}