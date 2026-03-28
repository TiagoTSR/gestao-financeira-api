package br.com.decodex.gestaofinanceira.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.decodex.gestaofinanceira.dto.Anexo;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoria;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaDia;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaPessoa;
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.projection.ResumoLancamento;
import br.com.decodex.gestaofinanceira.service.LancamentoService;
import br.com.decodex.gestaofinanceira.service.QueryParamValidator;
import br.com.decodex.gestaofinanceira.storage.S3;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/lancamentos")
@CrossOrigin("http://localhost:4200")
public class LancamentoController {
	
	private final LancamentoService lancamentoService;
	
	@Autowired
	private S3 s3;
	
	public LancamentoController(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}
	
	@PostMapping(value = "/anexo", consumes = "multipart/form-data")
	public Anexo uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
		String nome = s3.salvarTemporariamente(anexo);
		return new Anexo(nome, s3.configurarUrl(nome));
	}
			
	/*
	@PostMapping("/anexo")
	public String uploadAnexo(@RequestParam MultipartFile anexo) throws IOException {
		OutputStream out = new FileOutputStream("C:\\Tiago\\anexos--" + anexo.getOriginalFilename());
		out.write(anexo.getBytes());
		out.close();
		return "ok";
	}
	*/
	@GetMapping("/relatorios/por-pessoa")
	public ResponseEntity<byte[]> relatorioPorPessoa(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim) throws Exception {
		byte[] relatorio = lancamentoService.relatorioPorPessoa(inicio, fim);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.body(relatorio);
	}
	
	@GetMapping("/estatisticas/por-pessoa")
    public List<LancamentoEstatisticaPessoa> porPessoa(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
		return this.lancamentoService.porPessoa(inicio, fim);
	}
	
	@GetMapping("/estatisticas/por-categoria")
    public List<LancamentoEstatisticaCategoria> porCategoria(
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
		return this.lancamentoService.porCategoria(inicio, fim);
	}
	
	@GetMapping("/estatisticas/por-dia")
	public List<LancamentoEstatisticaDia> porDia(
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
	    return lancamentoService.porDia(inicio, fim);
	}

	@GetMapping("/resumo")
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter,@PageableDefault(size = 10) Pageable pageable) {
	    return lancamentoService.resumir(lancamentoFilter, pageable);
	}
	
	@GetMapping("/listAll")
    public ResponseEntity<Page<LancamentoResponseDTO>> findAll(
            LancamentoFilter lancamentoFilter,
            Pageable pageable,
            HttpServletRequest request) {

    	 QueryParamValidator.validate(request,Set.of("descricao", "dataVencimentoDe", "dataVencimentoAte"));

        Page<LancamentoResponseDTO> page = lancamentoService.findAll(lancamentoFilter, pageable);
        return ResponseEntity.ok(page);
    }
	
	@GetMapping("/findById/{id}")
	public ResponseEntity<LancamentoResponseDTO> findById(@PathVariable Long id){
		return ResponseEntity.ok(lancamentoService.findByIdDTO(id));
	}
	
	@PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LancamentoResponseDTO> create(
	        @Valid @RequestBody LancamentoRequestDTO dto) {
	    return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoService.create(dto, null));
	}

	@PostMapping(value = "/save/anexo")
	public ResponseEntity<LancamentoResponseDTO> createComAnexo(
	        @RequestPart("dados") @Valid LancamentoRequestDTO dto,
	        @RequestPart(value = "anexo", required = false) MultipartFile anexo) {
	    return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoService.create(dto, anexo));
	}

	@PutMapping(value = "/update/{id}")
	public ResponseEntity<LancamentoResponseDTO> update(
	        @PathVariable Long id,
	        @Valid @RequestBody LancamentoRequestDTO dto) {
	    return ResponseEntity.ok(lancamentoService.update(id, dto, null));
	}

	@PutMapping(value = "/update/{id}/anexo")
	public ResponseEntity<LancamentoResponseDTO> updateComAnexo(
	        @PathVariable Long id,
	        @RequestPart("dados") @Valid LancamentoRequestDTO dto,
	        @RequestPart(value = "anexo", required = false) MultipartFile anexo) {
	    return ResponseEntity.ok(lancamentoService.update(id, dto, anexo));
	}

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lancamentoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}