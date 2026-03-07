package br.com.decodex.gestaofinanceira.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.CategoriaMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;

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
}