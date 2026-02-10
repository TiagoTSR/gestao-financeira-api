package br.com.decodex.gestaofinanceira.controller;

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

import br.com.decodex.gestaofinanceira.dto.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.service.LancamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
	
	private final LancamentoService lancamentoService;
	
	public LancamentoController(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}
	
	@GetMapping("/listAll")
    public ResponseEntity<Page<LancamentoResponseDTO>> findAll(
            LancamentoFilter lancamentoFilter,
            Pageable pageable
    ) {

        Page<LancamentoResponseDTO> page = lancamentoService.findAll(lancamentoFilter, pageable);

        if (page.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(page);
    }
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<LancamentoResponseDTO> findById(@PathVariable Long id){
		return ResponseEntity.ok(lancamentoService.findByIdDTO(id));
	}
	
	@PostMapping("/save")
	public ResponseEntity<LancamentoResponseDTO> create(@Valid @RequestBody LancamentoRequestDTO dto) {
		LancamentoResponseDTO created = lancamentoService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LancamentoResponseDTO> update(@PathVariable Long id,@Valid @RequestBody LancamentoRequestDTO dto) {
        return ResponseEntity.ok(lancamentoService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lancamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}