package br.com.decodex.gestaofinanceira.controller;

import java.util.List;

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
import br.com.decodex.gestaofinanceira.service.PessoaService;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {
	
	private final PessoaService pessoaService;
	
	public PessoaController(PessoaService pessoaService) {
		this.pessoaService = pessoaService;
	}
	
	@GetMapping("/findAll")
	public ResponseEntity<List<Pessoa>> findAll(){
		return ResponseEntity.ok(pessoaService.findAll());
	}
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<Pessoa> findById(@PathVariable Long id){
		return ResponseEntity.ok(pessoaService.findById(id));
	}
	
	@PostMapping("/save")
    public ResponseEntity<Pessoa> create(@RequestBody Pessoa pessoa) {
        Pessoa created = pessoaService.create(pessoa);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Pessoa> update(
            @PathVariable Long id,
            @RequestBody Pessoa pessoa) {

        pessoa.setId(id);
        return ResponseEntity.ok(pessoaService.update(pessoa));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pessoaService.delete(id);
        return ResponseEntity.noContent().build();
    }

}