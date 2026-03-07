package br.com.decodex.gestaofinanceira.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.model.Categoria;

class CategoriaMapperTest {

    private CategoriaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoriaMapper();
    }

    @Test
    @DisplayName("Deve converter CategoriaRequestDTO para Entidade")
    void toEntityShouldMapDataCorrectly() {
        CategoriaRequestDTO dto = new CategoriaRequestDTO("Lazer");

        Categoria result = mapper.toEntity(dto);

        assertThat(result).isNotNull();
        assertThat(result.getNome()).isEqualTo(dto.nome());
        assertThat(result.getId()).isNull();
    }

    @Test
    @DisplayName("Deve converter Entidade para CategoriaResponseDTO")
    void toDTOShouldMapDataCorrectly() {
        Categoria entity = new Categoria();
        entity.setId(10L);
        entity.setNome("Saúde");

        CategoriaResponseDTO result = mapper.toDTO(entity);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(entity.getId());
        assertThat(result.nome()).isEqualTo(entity.getNome());
    }

    @Test
    @DisplayName("Deve atualizar a entidade existente com dados do DTO")
    void updateEntityShouldUpdateFieldsCorrectly() {
        Categoria entity = new Categoria();
        entity.setId(1L);
        entity.setNome("Antigo Nome");

        CategoriaRequestDTO dto = new CategoriaRequestDTO("Novo Nome");

        mapper.updateEntity(entity, dto);

        assertThat(entity.getNome()).isEqualTo("Novo Nome");
        assertThat(entity.getId()).isEqualTo(1L);
    }
}