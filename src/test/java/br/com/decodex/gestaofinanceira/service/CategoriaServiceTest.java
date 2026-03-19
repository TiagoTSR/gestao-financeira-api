package br.com.decodex.gestaofinanceira.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.CategoriaMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.CategoriaFilter;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @Mock
    private CategoriaMapper mapper;

    @InjectMocks
    private CategoriaService service;

    private Categoria categoria;
    private CategoriaResponseDTO responseDTO;
    private CategoriaRequestDTO requestDTO;
    private final Long existingId = 1L;
    private final Long nonExistingId = 2L;

    @BeforeEach
    void setUp() {
        requestDTO = new CategoriaRequestDTO("Alimentação");
        responseDTO = new CategoriaResponseDTO(existingId, "Alimentação");

        categoria = new Categoria();
        categoria.setId(existingId);
        categoria.setNome("Alimentação");
    }

    @Test
    @DisplayName("Deve retornar uma Categoria quando o ID existir")
    void findByIdShouldReturnCategoriaWhenIdExists() {
        when(repository.findById(existingId)).thenReturn(Optional.of(categoria));

        Categoria result = service.findById(existingId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingId);
        assertThat(result.getNome()).isEqualTo("Alimentação");
        verify(repository, times(1)).findById(existingId);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(nonExistingId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Categoria não encontrada: " + nonExistingId);
    }

    @Test
    @DisplayName("Deve retornar um DTO quando buscar por ID")
    void findByIdDTOShouldReturnResponseDTOWhenIdExists() {
        when(repository.findById(existingId)).thenReturn(Optional.of(categoria));
        when(mapper.toDTO(categoria)).thenReturn(responseDTO);

        CategoriaResponseDTO result = service.findByIdDTO(existingId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(existingId);
        assertThat(result.nome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve criar uma categoria com sucesso")
    void createShouldReturnCategoriaResponseDTO() {
        when(mapper.toEntity(requestDTO)).thenReturn(categoria);
        when(repository.save(any())).thenReturn(categoria);
        when(mapper.toDTO(categoria)).thenReturn(responseDTO);

        CategoriaResponseDTO result = service.create(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.nome()).isEqualTo(requestDTO.nome());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Deve atualizar uma categoria quando o ID existir")
    void updateShouldReturnUpdatedCategoriaResponseDTO() {
        when(repository.findById(existingId)).thenReturn(Optional.of(categoria));
        when(repository.save(any())).thenReturn(categoria);
        when(mapper.toDTO(categoria)).thenReturn(responseDTO);

        CategoriaResponseDTO result = service.update(existingId, requestDTO);

        assertThat(result).isNotNull();
        verify(mapper).updateEntity(categoria, requestDTO);
        verify(repository).save(categoria);
    }

    @Test
    @DisplayName("Deve deletar a categoria quando o ID existir")
    void deleteShouldDoNothingWhenIdExists() {
        when(repository.findById(existingId)).thenReturn(Optional.of(categoria));

        service.delete(existingId);

        verify(repository, times(1)).delete(categoria);
    }
    
    @Test
    @DisplayName("Deve retornar lista simples de categorias")
    void findAllSimpleShouldReturnListOfResponseDTOs() {
        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNome("Lazer");
        CategoriaResponseDTO responseDTO2 = new CategoriaResponseDTO(2L, "Lazer");

        when(repository.findAll()).thenReturn(List.of(categoria, categoria2));
        when(mapper.toDTO(categoria)).thenReturn(responseDTO);
        when(mapper.toDTO(categoria2)).thenReturn(responseDTO2);

        List<CategoriaResponseDTO> result = service.findAllSimple();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nome()).isEqualTo("Alimentação");
        assertThat(result.get(1).nome()).isEqualTo("Lazer");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve retornar página de categorias filtrada")
    void findAllShouldReturnPageOfCategorias() {
        Page<Categoria> page = new PageImpl<>(List.of(categoria), PageRequest.of(0, 10), 1);

        when(repository.findAll(
                ArgumentMatchers.<Specification<Categoria>>any(),
                any(Pageable.class)))
                .thenReturn(page);
        when(mapper.toDTO(categoria)).thenReturn(responseDTO);

        Page<CategoriaResponseDTO> result = service.findAll(new CategoriaFilter(), PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).nome()).isEqualTo("Alimentação");
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria com ID inexistente")
    void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(nonExistingId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoria não encontrada: " + nonExistingId);

        verify(repository, times(0)).deleteById(any());
    }
}