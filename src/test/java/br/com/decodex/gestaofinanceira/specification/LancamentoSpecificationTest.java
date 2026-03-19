package br.com.decodex.gestaofinanceira.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.LancamentoRepository;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;
import br.com.decodex.gestaofinanceira.repository.specification.LancamentoSpecification;

@DataJpaTest
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=VALUE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create",  // ✅ cria as tabelas
        "spring.flyway.enabled=false"        
})
class LancamentoSpecificationTest {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Pessoa pessoa1;
    private Pessoa pessoa2;
    private Categoria categoriaLazer;
    private Categoria categoriaAlimentacao;

    @BeforeEach
    void setUp() {
        lancamentoRepository.deleteAll();

        pessoa1 = new Pessoa();
        pessoa1.setNome("João Silva");
        pessoa1.setAtivo(true);
        pessoaRepository.save(pessoa1);

        pessoa2 = new Pessoa();
        pessoa2.setNome("Maria Souza");
        pessoa2.setAtivo(true);
        pessoaRepository.save(pessoa2);

        categoriaLazer = new Categoria();
        categoriaLazer.setNome("Lazer");
        categoriaRepository.save(categoriaLazer);

        categoriaAlimentacao = new Categoria();
        categoriaAlimentacao.setNome("Alimentação");
        categoriaRepository.save(categoriaAlimentacao);

        // Lançamento 1 — pessoa1, lazer, baixo valor
        Lancamento l1 = new Lancamento();
        l1.setDescricao("Cinema");
        l1.setDataVencimento(LocalDate.of(2026, 1, 10));
        l1.setValor(new BigDecimal("50.00"));
        l1.setTipo(TipoLancamento.DESPESA);
        l1.setPessoa(pessoa1);
        l1.setCategoria(categoriaLazer);
        lancamentoRepository.save(l1);

        // Lançamento 2 — pessoa2, alimentação, médio valor
        Lancamento l2 = new Lancamento();
        l2.setDescricao("Supermercado");
        l2.setDataVencimento(LocalDate.of(2026, 2, 15));
        l2.setValor(new BigDecimal("300.00"));
        l2.setTipo(TipoLancamento.DESPESA);
        l2.setPessoa(pessoa2);
        l2.setCategoria(categoriaAlimentacao);
        lancamentoRepository.save(l2);

        // Lançamento 3 — pessoa1, alimentação, alto valor
        Lancamento l3 = new Lancamento();
        l3.setDescricao("Salário");
        l3.setDataVencimento(LocalDate.of(2026, 3, 1));
        l3.setValor(new BigDecimal("5000.00"));
        l3.setTipo(TipoLancamento.RECEITA);
        l3.setPessoa(pessoa1);
        l3.setCategoria(categoriaAlimentacao);
        lancamentoRepository.save(l3);
    }

    @Test
    @DisplayName("Filtro nulo deve retornar todos os lançamentos")
    void filtrarComFiltroNuloDeveRetornarTodos() {
        Specification<Lancamento> spec = LancamentoSpecification.filtrar(null);

        List<Lancamento> result = lancamentoRepository.findAll(spec);

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Filtro vazio deve retornar todos os lançamentos")
    void filtrarComFiltroVazioDeveRetornarTodos() {
        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(new LancamentoFilter()));

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Deve filtrar por descrição ignorando case")
    void filtrarPorDescricaoDeveRetornarCorrespondentes() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDescricao("cinema");

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Cinema");
    }

    @Test
    @DisplayName("Deve filtrar por descrição parcial")
    void filtrarPorDescricaoParcialDeveRetornarCorrespondentes() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDescricao("super");

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Supermercado");
    }

    @Test
    @DisplayName("Deve filtrar por dataVencimentoDe")
    void filtrarPorDataVencimentoDeDeveRetornarAPartirDaData() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDataVencimentoDe(LocalDate.of(2026, 2, 1));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Deve filtrar por dataVencimentoAte")
    void filtrarPorDataVencimentoAteDeveRetornarAteAData() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDataVencimentoAte(LocalDate.of(2026, 1, 31));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Cinema");
    }

    @Test
    @DisplayName("Deve filtrar por intervalo de datas")
    void filtrarPorIntervaloDatasDeveRetornarApenas() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDataVencimentoDe(LocalDate.of(2026, 1, 1));
        filter.setDataVencimentoAte(LocalDate.of(2026, 2, 28));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Deve filtrar por valor mínimo")
    void filtrarPorValorMinDeveRetornarAcimaDoValor() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setValorMin(new BigDecimal("100.00"));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Deve filtrar por valor máximo")
    void filtrarPorValorMaxDeveRetornarAbaixoDoValor() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setValorMax(new BigDecimal("100.00"));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Cinema");
    }

    @Test
    @DisplayName("Deve filtrar por intervalo de valores")
    void filtrarPorIntervaloDeValoresDeveRetornarApenas() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setValorMin(new BigDecimal("100.00"));
        filter.setValorMax(new BigDecimal("400.00"));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Supermercado");
    }

    @Test
    @DisplayName("Deve filtrar por pessoaId")
    void filtrarPorPessoaIdDeveRetornarApenasDaPessoa() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setPessoaId(pessoa1.getId());

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(l -> l.getPessoa().getId().equals(pessoa1.getId()));
    }

    @Test
    @DisplayName("Deve filtrar por categoriaId")
    void filtrarPorCategoriaIdDeveRetornarApenasDestaCategoria() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setCategoriaId(categoriaLazer.getId());

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Cinema");
    }

    @Test
    @DisplayName("Deve filtrar por pessoaId e valorMax combinados")
    void filtrarPorPessoaEValorMaxDeveRetornarInterseccao() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setPessoaId(pessoa1.getId());
        filter.setValorMax(new BigDecimal("100.00"));

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Cinema");
    }

    @Test
    @DisplayName("Deve filtrar por pessoaId e categoriaId juntos sem duplicatas")
    void filtrarPorPessoaECategoriaJuntosNaoDeveDuplicar() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setPessoaId(pessoa1.getId());
        filter.setCategoriaId(categoriaAlimentacao.getId());

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDescricao()).isEqualTo("Salário");
    }

    @Test
    @DisplayName("Deve retornar vazio quando nenhum lançamento corresponder ao filtro")
    void filtrarSemCorrespondenciasDeveRetornarVazio() {
        LancamentoFilter filter = new LancamentoFilter();
        filter.setDescricao("xyz-inexistente");

        List<Lancamento> result = lancamentoRepository.findAll(
                LancamentoSpecification.filtrar(filter));

        assertThat(result).isEmpty();
    }
}