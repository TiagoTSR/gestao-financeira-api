package br.com.decodex.gestaofinanceira.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaCategoria;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaDia;
import br.com.decodex.gestaofinanceira.dto.estatisticas.LancamentoEstatisticaPessoa;
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.LancamentoMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.LancamentoRepository;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.projection.ResumoLancamento;
import br.com.decodex.gestaofinanceira.repository.specification.LancamentoSpecification;
import br.com.decodex.gestaofinanceira.storage.S3;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService {
	
	private final LancamentoRepository lancamentoRepository;
	private final PessoaRepository pessoaRepository;
    private final CategoriaRepository categoriaRepository;
    private final LancamentoMapper mapper;
    
    @Autowired
    private S3 s3;
    
	public LancamentoService(LancamentoRepository lancamentoRepository,
			PessoaRepository pessoaRepository,
            CategoriaRepository categoriaRepository,
            LancamentoMapper mapper) {
	     this.lancamentoRepository = lancamentoRepository;
	     this.pessoaRepository = pessoaRepository;
	     this.categoriaRepository = categoriaRepository;
	     this.mapper = mapper;
	}
	
	@Scheduled(cron = "0 0 7 * * *")
	public void avisarSobreLancamentosVencidos() {
		System.out.println(">>>>>>>>>>>>>>> Método sendo executado...");
	}
	
	public byte[] relatorioPorPessoa(LocalDate inicio, LocalDate fim) throws Exception {
	    List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(inicio, fim);
	    
	    Map<String, Object> parametros = new HashMap<>();
	    parametros.put("DT_INICIO", Date.valueOf(inicio));
	    parametros.put("DT_FIM", Date.valueOf(fim));
	    parametros.put("REPORT_LOCALE", Locale.of("pt", "BR"));
	    
	    InputStream inputStream = this.getClass().getResourceAsStream(
	            "/relatorios/lancamentos-por-pessoa.jrxml");
	    
	    if (inputStream == null) {
	        throw new FileNotFoundException("Arquivo .jrxml não encontrado em /relatorios/");
	    }

	    JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
	    
	    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros,
	            new JRBeanCollectionDataSource(dados));
	    
	    return JasperExportManager.exportReportToPdf(jasperPrint);
	}
	
	@Transactional(readOnly = true)
	public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate inicio, LocalDate fim) {
	    return lancamentoRepository.porPessoa(inicio, fim);
	}
	
	@Transactional(readOnly = true)
	public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate inicio, LocalDate fim) {
	    return lancamentoRepository.porCategoria(inicio, fim);
	}
	
	@Transactional(readOnly = true)
	public List<LancamentoEstatisticaDia> porDia(LocalDate inicio, LocalDate fim) {
	    return lancamentoRepository.porDia(inicio, fim);
	}

	@Transactional(readOnly = true)
	public Page<ResumoLancamento> resumir(LancamentoFilter filter, Pageable pageable) {
	    return lancamentoRepository.resumir(filter, pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<LancamentoResponseDTO> findAll(LancamentoFilter filter, Pageable pageable) {
	    Specification<Lancamento> spec = LancamentoSpecification.filtrar(filter);
	    
	    return lancamentoRepository.findAll(spec, pageable).map(mapper::toDTO);
	}
	
    @Transactional(readOnly = true)
    public Lancamento findById(Long id) {

        return lancamentoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Lançamento não encontrado para o ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public LancamentoResponseDTO findByIdDTO(Long id) {
        return mapper.toDTO(findById(id));
    }
    
    @Transactional
    public LancamentoResponseDTO create(LancamentoRequestDTO dto, MultipartFile anexo) {

        Pessoa pessoa = pessoaRepository.findById(dto.pessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada: " + dto.pessoaId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + dto.categoriaId()));

        String urlAnexo = dto.urlAnexo();
        String nomeAnexo = dto.anexo();

        if (anexo != null && !anexo.isEmpty()) {
            urlAnexo = s3.salvarTemporariamente(anexo);
            nomeAnexo = anexo.getOriginalFilename();
        }

        Lancamento lancamento = mapper.toEntity(dto, pessoa, categoria);
        lancamento.setUrlAnexo(urlAnexo);
        lancamento.setAnexo(nomeAnexo);

        return mapper.toDTO(lancamentoRepository.save(lancamento));
    }
    
    @Transactional
    public LancamentoResponseDTO update(Long id, LancamentoRequestDTO dto, MultipartFile anexo) {

        Lancamento existente = findById(id);

        Pessoa pessoa = pessoaRepository.findById(dto.pessoaId())
                .orElseThrow(() -> new ResourceNotFoundException("Pessoa não encontrada para o ID: " + dto.pessoaId()));

        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada para o ID: " + dto.categoriaId()));

        String urlAnexo = dto.urlAnexo();
        String nomeAnexo = dto.anexo();

        if (anexo != null && !anexo.isEmpty()) {
   
            nomeAnexo = s3.salvarTemporariamente(anexo);
            urlAnexo = s3.configurarUrl(nomeAnexo);
        }

        if (StringUtils.hasText(nomeAnexo)) {
            s3.create(nomeAnexo);
        }

        mapper.updateEntity(existente, dto, pessoa, categoria);

        existente.setPessoa(pessoa);
        existente.setCategoria(categoria);
        existente.setAnexo(nomeAnexo);
        existente.setUrlAnexo(urlAnexo);

        return mapper.toDTO(lancamentoRepository.save(existente));
    }

    public void delete(Long id) {
        Lancamento lancamento = findById(id);
        lancamentoRepository.deleteById(lancamento.getId());
    }
}