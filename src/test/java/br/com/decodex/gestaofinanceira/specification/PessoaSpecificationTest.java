package br.com.decodex.gestaofinanceira.specification;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import br.com.decodex.gestaofinanceira.model.Endereco;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import br.com.decodex.gestaofinanceira.repository.specification.PessoaSpecification;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=VALUE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.flyway.enabled=false"
})
@Transactional
class PessoaSpecificationTest {

    @Autowired
    private PessoaRepository pessoaRepository;

    @BeforeEach
    void setUp() {
        Endereco enderecoSP = new Endereco();
        enderecoSP.setLogradouro("Av. Paulista");
        enderecoSP.setNumero("1000");
        enderecoSP.setBairro("Bela Vista");
        enderecoSP.setCidade("São Paulo");
        enderecoSP.setEstado("SP");
        enderecoSP.setCep("01310-100");

        Pessoa pessoa1 = new Pessoa();
        pessoa1.setNome("João Silva");
        pessoa1.setAtivo(true);
        pessoa1.setEndereco(enderecoSP);
        pessoaRepository.save(pessoa1);

        Endereco enderecoRJ = new Endereco();
        enderecoRJ.setLogradouro("Rua Copacabana");
        enderecoRJ.setNumero("200");
        enderecoRJ.setBairro("Copacabana");
        enderecoRJ.setCidade("Rio de Janeiro");
        enderecoRJ.setEstado("RJ");
        enderecoRJ.setCep("22020-000");

        Pessoa pessoa2 = new Pessoa();
        pessoa2.setNome("Maria Souza");
        pessoa2.setAtivo(false);
        pessoa2.setEndereco(enderecoRJ);
        pessoaRepository.save(pessoa2);

        Pessoa pessoa3 = new Pessoa();
        pessoa3.setNome("João Carlos");
        pessoa3.setAtivo(true);
        pessoa3.setEndereco(enderecoSP);
        pessoaRepository.save(pessoa3);
    }

    @Test
    @DisplayName("Filtro vazio deve retornar todas as pessoas")
    void filtrarComFiltroVazioDeveRetornarTodas() {
        PessoaFilter filter = new PessoaFilter();

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Deve filtrar por nome ignorando case")
    void filtrarPorNomeDeveRetornarCorrespondentes() {
        PessoaFilter filter = new PessoaFilter();
        filter.setNome("joão");

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getNome().toLowerCase().contains("joão"));
    }

    @Test
    @DisplayName("Deve filtrar por nome parcial")
    void filtrarPorNomeParcialDeveRetornarCorrespondentes() {
        PessoaFilter filter = new PessoaFilter();
        filter.setNome("silva");

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve filtrar por ativo true")
    void filtrarPorAtivoTrueDeveRetornarApenasAtivos() {
        PessoaFilter filter = new PessoaFilter();
        filter.setAtivo(true);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Pessoa::getAtivo);
    }

    @Test
    @DisplayName("Deve filtrar por ativo false")
    void filtrarPorAtivoFalseDeveRetornarApenasInativos() {
        PessoaFilter filter = new PessoaFilter();
        filter.setAtivo(false);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Maria Souza");
    }

    @Test
    @DisplayName("Deve filtrar por cidade ignorando case")
    void filtrarPorCidadeDeveRetornarCorrespondentes() {
        PessoaFilter filter = new PessoaFilter();
        Endereco enderecoFiltro = new Endereco();
        enderecoFiltro.setCidade("são paulo");
        filter.setEndereco(enderecoFiltro);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p -> p.getEndereco().getCidade().equals("São Paulo"));
    }

    @Test
    @DisplayName("Deve filtrar por cidade parcial")
    void filtrarPorCidadeParcialDeveRetornarCorrespondentes() {
        PessoaFilter filter = new PessoaFilter();
        Endereco enderecoFiltro = new Endereco();
        enderecoFiltro.setCidade("rio");
        filter.setEndereco(enderecoFiltro);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Maria Souza");
    }

    @Test
    @DisplayName("Deve combinar filtro de nome e ativo")
    void filtrarPorNomeEAtivoDeveRetornarInterseccao() {
        PessoaFilter filter = new PessoaFilter();
        filter.setNome("joão");
        filter.setAtivo(true);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(p ->
                p.getNome().toLowerCase().contains("joão") && p.getAtivo());
    }

    @Test
    @DisplayName("Deve combinar filtro de nome, ativo e cidade")
    void filtrarComTresFiltrosDeveRetornarInterseccao() {
        PessoaFilter filter = new PessoaFilter();
        filter.setNome("silva");
        filter.setAtivo(true);
        Endereco enderecoFiltro = new Endereco();
        enderecoFiltro.setCidade("são paulo");
        filter.setEndereco(enderecoFiltro);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("João Silva");
    }

    @Test
    @DisplayName("Deve retornar vazio quando nenhuma pessoa corresponder ao filtro")
    void filtrarSemCorrespondenciasDeveRetornarVazio() {
        PessoaFilter filter = new PessoaFilter();
        filter.setNome("xyz-inexistente");

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve ignorar filtro de cidade quando endereço for nulo")
    void filtrarComEnderecoNuloNaoDeveAplicarFiltroDeCidade() {
        PessoaFilter filter = new PessoaFilter();
        filter.setEndereco(null);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Deve ignorar filtro de cidade quando cidade for vazia")
    void filtrarComCidadeVaziaNaoDeveAplicarFiltroDeCidade() {
        PessoaFilter filter = new PessoaFilter();
        Endereco enderecoFiltro = new Endereco();
        enderecoFiltro.setCidade("");
        filter.setEndereco(enderecoFiltro);

        List<Pessoa> result = pessoaRepository.findAll(
                PessoaSpecification.filtrar(filter));

        assertThat(result).hasSize(3);
    }
}