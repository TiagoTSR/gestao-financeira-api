package br.com.decodex.gestaofinanceira.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.repository.filter.CategoriaFilter;
import br.com.decodex.gestaofinanceira.service.CategoriaService;
import br.com.decodex.gestaofinanceira.service.QueryParamValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

	private final CategoriaService categoriaService;
	
	public CategoriaController(CategoriaService categoriaService) {
		this.categoriaService = categoriaService;
	}
	
	@GetMapping("/listAll")
    public ResponseEntity<Page<CategoriaResponseDTO>> findAll(
            CategoriaFilter categoriaFilter,
            Pageable pageable,
            HttpServletRequest request) {

    	QueryParamValidator.validate(request, Set.of("nome"));

        Page<CategoriaResponseDTO> page = categoriaService.findAll(categoriaFilter, pageable);
        return ResponseEntity.ok(page);
    }
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<CategoriaResponseDTO> findById(@PathVariable Long id){
		return ResponseEntity.ok(categoriaService.findByIdDTO(id));
	}	
	
	@PostMapping("/save")
    public ResponseEntity<CategoriaResponseDTO> create(@Valid @RequestBody CategoriaRequestDTO dto) {
		CategoriaResponseDTO created = categoriaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoriaResponseDTO> update(@PathVariable Long id,@Valid @RequestBody CategoriaRequestDTO dto) {
        return ResponseEntity.ok(categoriaService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}