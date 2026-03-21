package br.com.decodex.gestaofinanceira.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import java.util.List;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.repository.filter.PessoaFilter;
import br.com.decodex.gestaofinanceira.dto.pessoa.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.pessoa.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.mapper.PessoaMapper;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;

@ExtendWith(MockitoExtension.class)
class PessoaServiceTest {

    @Mock
    private PessoaRepository repository;

    @Mock
    private PessoaMapper mapper;

    @InjectMocks
    private PessoaService service;

    private Pessoa pessoa;
    private PessoaRequestDTO requestDTO;
    private PessoaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("Ana Silva");
        pessoa.setAtivo(true);

        requestDTO = new PessoaRequestDTO("Ana Silva", null, true);
        responseDTO = new PessoaResponseDTO(1L, "Ana Silva", null, true);
    }

    @Test
    @DisplayName("Deve criar uma pessoa e garantir que esteja ativa por padrão")
    void createShouldReturnPessoaAtiva() {
        PessoaRequestDTO dtoComAtivoNull = new PessoaRequestDTO("Ana Silva", null, null);
        Pessoa pessoaSemStatus = new Pessoa();
        pessoaSemStatus.setAtivo(null);

        when(mapper.toEntity(dtoComAtivoNull)).thenReturn(pessoaSemStatus);
        when(repository.save(any())).thenReturn(pessoaSemStatus);
        when(mapper.toDTO(any())).thenReturn(responseDTO);

        service.create(dtoComAtivoNull);

        assertThat(pessoaSemStatus.getAtivo()).isTrue();
        verify(repository).save(pessoaSemStatus);
    }

    @Test
    @DisplayName("Deve deletar pessoa quando encontrar o ID")
    void deleteShouldCallRepositoryWhenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));

        service.delete(1L);

        verify(repository, times(1)).deleteById(any());
    }
    
    @Test
    @DisplayName("Deve retornar pessoa quando ID existir")
    void findByIdShouldReturnPessoaWhenIdExists() {
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));

        Pessoa result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getNome()).isEqualTo("Ana Silva");
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando ID não existir")
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");
    }

    @Test
    @DisplayName("Deve retornar DTO ao buscar pessoa por ID")
    void findByIdDTOShouldReturnResponseDTO() {
        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(mapper.toDTO(pessoa)).thenReturn(responseDTO);

        PessoaResponseDTO result = service.findByIdDTO(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("Ana Silva");
    }

    @Test
    @DisplayName("Deve atualizar pessoa com sucesso")
    void updateShouldReturnUpdatedResponseDTO() {
        PessoaRequestDTO updateRequest = new PessoaRequestDTO("Ana Souza", null, false);
        PessoaResponseDTO updatedResponse = new PessoaResponseDTO(1L, "Ana Souza", null, false);

        when(repository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(repository.save(any())).thenReturn(pessoa);
        when(mapper.toDTO(pessoa)).thenReturn(updatedResponse);

        PessoaResponseDTO result = service.update(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.nome()).isEqualTo("Ana Souza");
        verify(mapper).updateEntity(pessoa, updateRequest);
        verify(repository).save(pessoa);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar pessoa com ID inexistente")
    void updateShouldThrowExceptionWhenIdDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar pessoa com ID inexistente")
    void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");

        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar lista simples de pessoas")
    void findAllSimpleShouldReturnListOfResponseDTOs() {
        Pessoa pessoa2 = new Pessoa();
        pessoa2.setId(2L);
        pessoa2.setNome("Carlos Lima");
        pessoa2.setAtivo(false);
        PessoaResponseDTO responseDTO2 = new PessoaResponseDTO(2L, "Carlos Lima", null, false);

        when(repository.findAll()).thenReturn(List.of(pessoa, pessoa2));
        when(mapper.toDTO(pessoa)).thenReturn(responseDTO);
        when(mapper.toDTO(pessoa2)).thenReturn(responseDTO2);

        List<PessoaResponseDTO> result = service.findAllSimple();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nome()).isEqualTo("Ana Silva");
        assertThat(result.get(1).nome()).isEqualTo("Carlos Lima");
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Deve retornar página de pessoas filtrada")
    void findAllShouldReturnPageOfPessoas() {
        Page<Pessoa> page = new PageImpl<>(List.of(pessoa), PageRequest.of(0, 10), 1);

        when(repository.findAll(
                ArgumentMatchers.<Specification<Pessoa>>any(),
                any(Pageable.class)))
                .thenReturn(page);
        when(mapper.toDTO(pessoa)).thenReturn(responseDTO);

        Page<PessoaResponseDTO> result = service.findAll(new PessoaFilter(), PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).nome()).isEqualTo("Ana Silva");
    }
}
