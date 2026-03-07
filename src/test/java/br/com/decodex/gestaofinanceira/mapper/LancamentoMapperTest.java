package br.com.decodex.gestaofinanceira.mapper;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.decodex.gestaofinanceira.dto.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;

class LancamentoMapperTest {

    private LancamentoMapper mapper;
    private Pessoa pessoa;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        mapper = new LancamentoMapper();

        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("Maria Silva");

        categoria = new Categoria();
        categoria.setId(2L);
        categoria.setNome("Alimentação");
    }

    @Test
    @DisplayName("Deve converter LancamentoRequestDTO para Entidade com relacionamentos")
    void toEntityShouldMapAllFieldsAndRelationships() {
        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                "Supermercado", 
                LocalDate.now(), 
                null, 
                new BigDecimal("150.00"), 
                "Compra do mês", 
                TipoLancamento.DESPESA, 
                2L, 
                1L
        );

        Lancamento result = mapper.toEntity(dto, pessoa, categoria);

        assertThat(result).isNotNull();
        assertThat(result.getDescricao()).isEqualTo(dto.descricao());
        assertThat(result.getValor()).isEqualTo(dto.valor());
        assertThat(result.getPessoa()).isEqualTo(pessoa);
        assertThat(result.getCategoria()).isEqualTo(categoria);
        assertThat(result.getTipo()).isEqualTo(TipoLancamento.DESPESA);
    }

    @Test
    @DisplayName("Deve converter Entidade para LancamentoResponseDTO extraindo dados aninhados")
    void toDTOShouldMapNestedEntityData() {
        Lancamento entity = new Lancamento();
        entity.setId(10L);
        entity.setDescricao("Salário");
        entity.setValor(new BigDecimal("5000.00"));
        entity.setTipo(TipoLancamento.RECEITA);
        entity.setDataVencimento(LocalDate.now());
        entity.setPessoa(pessoa);
        entity.setCategoria(categoria);

        LancamentoResponseDTO result = mapper.toDTO(entity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.descricao()).isEqualTo("Salário");
        
        assertThat(result.pessoaId()).isEqualTo(1L);
        assertThat(result.pessoaNome()).isEqualTo("Maria Silva");
        assertThat(result.categoriaId()).isEqualTo(2L);
        assertThat(result.categoriaNome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve atualizar entidade existente mantendo os relacionamentos passados")
    void updateEntityShouldUpdateAllFields() {
        Lancamento existente = new Lancamento();
        existente.setId(1L);
        existente.setDescricao("Antigo");

        LancamentoRequestDTO dto = new LancamentoRequestDTO(
                "Novo", LocalDate.now(), null, new BigDecimal("10.00"), 
                null, TipoLancamento.RECEITA, 2L, 1L
        );

        mapper.updateEntity(existente, dto, pessoa, categoria);

        assertThat(existente.getDescricao()).isEqualTo("Novo");
        assertThat(existente.getPessoa().getNome()).isEqualTo("Maria Silva");
        assertThat(existente.getTipo()).isEqualTo(TipoLancamento.RECEITA);
    }
}