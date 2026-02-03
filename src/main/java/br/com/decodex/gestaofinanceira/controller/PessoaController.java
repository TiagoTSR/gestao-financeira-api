package br.com.decodex.gestaofinanceira.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import br.com.decodex.gestaofinanceira.repository.specification.PessoaSpecification;
import br.com.decodex.gestaofinanceira.service.PessoaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {
	
	private final PessoaService pessoaService;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	public PessoaController(PessoaService pessoaService) {
		this.pessoaService = pessoaService;
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<Page<Pessoa>> findAll(
	        PessoaFilter pessoaFilter,
	        Pageable pageable) {

	    Page<Pessoa> page = pessoaRepository.findAll(
	            PessoaSpecification.filtrar(pessoaFilter),
	            pageable
	    );

	    if (page.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(page);
	}
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<Pessoa> findById(@PathVariable Long id){
		return ResponseEntity.ok(pessoaService.findById(id));
	}
	
	@PostMapping("/save")
    public ResponseEntity<Pessoa> create(@Valid @RequestBody Pessoa pessoa) {
        Pessoa created = pessoaService.create(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Pessoa> update(
            @PathVariable Long id,
            @Valid @RequestBody Pessoa pessoa) {

        pessoa.setId(id);
        return ResponseEntity.ok(pessoaService.update(pessoa));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pessoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}