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

import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import br.com.decodex.gestaofinanceira.service.PessoaService;
import br.com.decodex.gestaofinanceira.service.QueryParamValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {
	
	private final PessoaService pessoaService;
	
	public PessoaController(PessoaService pessoaService) {
		this.pessoaService = pessoaService;
	}
	
	@GetMapping("/listAll")
    public ResponseEntity<Page<PessoaResponseDTO>> findAll(
            PessoaFilter pessoaFilter,
            Pageable pageable,
            HttpServletRequest request) {

    	QueryParamValidator.validate(request, Set.of("nome", "cidade", "ativo"));

        Page<PessoaResponseDTO> page = pessoaService.findAll(pessoaFilter, pageable);
        return ResponseEntity.ok(page);
    }
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<PessoaResponseDTO> findById(@PathVariable Long id){
		return ResponseEntity.ok(pessoaService.findByIdDTO(id));
	}
	
	@PostMapping("/save")
	public ResponseEntity<PessoaResponseDTO> create(@Valid @RequestBody PessoaRequestDTO dto) {
		PessoaResponseDTO created = pessoaService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PessoaResponseDTO> update(@PathVariable Long id,@Valid @RequestBody PessoaRequestDTO dto) {
        return ResponseEntity.ok(pessoaService.update(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pessoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}