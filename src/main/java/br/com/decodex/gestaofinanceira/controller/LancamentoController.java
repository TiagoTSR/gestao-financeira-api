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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.repository.LancamentoRepository;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.specification.LancamentoSpecification;
import br.com.decodex.gestaofinanceira.service.LancamentoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoController {
	
	private final LancamentoService lancamentoService;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	public LancamentoController(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}
	
	@GetMapping("/listAll")
	public ResponseEntity<Page<Lancamento>> findAll(
	        LancamentoFilter lancamentoFilter,
	        Pageable pageable) {

	    Page<Lancamento> page = lancamentoRepository.findAll(
	            LancamentoSpecification.filtrar(lancamentoFilter),
	            pageable
	    );

	    if (page.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(page);
	}
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<Lancamento> findById(@PathVariable Long id){
		return ResponseEntity.ok(lancamentoService.findById(id));
	}
	
	@PostMapping("/save")
    @ResponseStatus(HttpStatus.CREATED)
    public Lancamento create(@Valid @RequestBody Lancamento lancamento) {
        return lancamentoService.create(lancamento);
    }

    @PutMapping("/update/{id}")
    public Lancamento update(
            @PathVariable Long id,
            @Valid @RequestBody Lancamento lancamento) {
        return lancamentoService.update(id, lancamento);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        lancamentoService.delete(id);
    }

}