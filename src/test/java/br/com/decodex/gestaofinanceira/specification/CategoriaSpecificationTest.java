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

import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.CategoriaFilter;
import br.com.decodex.gestaofinanceira.repository.specification.CategoriaSpecification;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL;NON_KEYWORDS=VALUE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.flyway.enabled=false"
})
@Transactional
class CategoriaSpecificationTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @BeforeEach
    void setUp() {
        Categoria alimentacao = new Categoria();
        alimentacao.setNome("Alimentação");
        categoriaRepository.save(alimentacao);

        Categoria lazer = new Categoria();
        lazer.setNome("Lazer");
        categoriaRepository.save(lazer);

        Categoria saude = new Categoria();
        saude.setNome("Saúde");
        categoriaRepository.save(saude);

        Categoria educacao = new Categoria();
        educacao.setNome("Educação");
        categoriaRepository.save(educacao);
    }

    @Test
    @DisplayName("Filtro nulo deve retornar todas as categorias")
    void filtrarComFiltroNuloDeveRetornarTodas() {
        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(null));

        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("Filtro vazio deve retornar todas as categorias")
    void filtrarComFiltroVazioDeveRetornarTodas() {
        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(new CategoriaFilter()));

        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("Deve filtrar por nome ignorando case")
    void filtrarPorNomeDeveRetornarCorrespondentes() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("alimentação");

        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve filtrar por nome parcial")
    void filtrarPorNomeParcialDeveRetornarCorrespondentes() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("edu");

        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Educação");
    }

    @Test
    @DisplayName("Deve filtrar por nome com maiúsculas e minúsculas misturadas")
    void filtrarPorNomeCaseMixedDeveRetornarCorrespondentes() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("LaZeR");

        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(filter));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNome()).isEqualTo("Lazer");
    }

    @Test
    @DisplayName("Deve retornar vazio quando nenhuma categoria corresponder ao filtro")
    void filtrarSemCorrespondenciasDeveRetornarVazio() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("xyz-inexistente");

        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(filter));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Deve ignorar filtro quando nome for string vazia")
    void filtrarComNomeVazioDeveRetornarTodas() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("");

        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(filter));

        assertThat(result).hasSize(4);
    }

    @Test
    @DisplayName("Deve ignorar filtro quando nome for apenas espaços")
    void filtrarComNomeApenasEspacosDeveRetornarTodas() {
        CategoriaFilter filter = new CategoriaFilter();
        filter.setNome("   ");

        List<Categoria> result = categoriaRepository.findAll(
                CategoriaSpecification.filtrar(filter));

        assertThat(result).hasSize(4);
    }
}