package br.com.decodex.gestaofinanceira.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.mockito.ArgumentMatchers;

import br.com.decodex.gestaofinanceira.dto.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.exceptions.ResourceNotFoundException;
import br.com.decodex.gestaofinanceira.mapper.LancamentoMapper;
import br.com.decodex.gestaofinanceira.model.Categoria;
import br.com.decodex.gestaofinanceira.model.Lancamento;
import br.com.decodex.gestaofinanceira.model.Pessoa;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;
import br.com.decodex.gestaofinanceira.repository.CategoriaRepository;
import br.com.decodex.gestaofinanceira.repository.LancamentoRepository;
import br.com.decodex.gestaofinanceira.repository.PessoaRepository;
import br.com.decodex.gestaofinanceira.repository.filter.LancamentoFilter;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceTest {

    @Mock
    private LancamentoRepository lancamentoRepository;
    @Mock
    private PessoaRepository pessoaRepository;
    @Mock
    private CategoriaRepository categoriaRepository;
    @Mock
    private LancamentoMapper mapper;

    @InjectMocks
    private LancamentoService service;

    private Lancamento lancamento;
    private LancamentoRequestDTO requestDTO;
    private LancamentoResponseDTO responseDTO;
    private Pessoa pessoa;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        pessoa = new Pessoa();
        pessoa.setId(1L);
        pessoa.setNome("João Silva");

        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Lazer");

        lancamento = new Lancamento();
        lancamento.setId(1L);
        lancamento.setDescricao("Cinema");
        lancamento.setPessoa(pessoa);
        lancamento.setCategoria(categoria);

        requestDTO = new LancamentoRequestDTO(
                "Cinema", LocalDate.now(), null, new BigDecimal("50.00"), 
                null, TipoLancamento.DESPESA, 1L, 1L
        );

        responseDTO = new LancamentoResponseDTO(
                1L, "Cinema", LocalDate.now(), null, new BigDecimal("50.00"),
                null, TipoLancamento.DESPESA, 1L, "Lazer", 1L, "João Silva"
        );
    }

    @Test
    @DisplayName("Deve criar um lançamento com sucesso quando pessoa e categoria existem")
    void createShouldReturnResponseDTOWhenDataIsValid() {
        // Arrange (Configuração dos mocks)
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(mapper.toEntity(any(), any(), any())).thenReturn(lancamento);
        when(lancamentoRepository.save(any())).thenReturn(lancamento);
        when(mapper.toDTO(any())).thenReturn(responseDTO);

        // Act
        LancamentoResponseDTO result = service.create(requestDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.descricao()).isEqualTo("Cinema");
        verify(lancamentoRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar lançamento com pessoa inexistente")
    void createShouldThrowExceptionWhenPessoaDoesNotExist() {
        when(pessoaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");
        
        verify(lancamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar lançamento por ID com sucesso")
    void findByIdShouldReturnLancamentoWhenIdExists() {
        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));

        Lancamento result = service.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve deletar lançamento com sucesso")
    void deleteShouldCallRepositoryWhenIdExists() {
        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));

        service.delete(1L);

        verify(lancamentoRepository, times(1)).deleteById(1L);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao criar lançamento com categoria inexistente")
    void createShouldThrowExceptionWhenCategoriaDoesNotExist() {
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoria não encontrada");

        verify(lancamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar lançamento com ID inexistente")
    void findByIdShouldThrowExceptionWhenIdDoesNotExist() {
        when(lancamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lançamento não encontrado para o ID: 99");
    }

    @Test
    @DisplayName("Deve retornar DTO ao buscar lançamento por ID")
    void findByIdDTOShouldReturnResponseDTO() {
        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));
        when(mapper.toDTO(lancamento)).thenReturn(responseDTO);

        LancamentoResponseDTO result = service.findByIdDTO(1L);

        assertThat(result).isNotNull();
        assertThat(result.descricao()).isEqualTo("Cinema");
        assertThat(result.pessoaNome()).isEqualTo("João Silva");
        assertThat(result.categoriaNome()).isEqualTo("Lazer");
    }

    @Test
    @DisplayName("Deve atualizar lançamento com sucesso quando dados são válidos")
    void updateShouldReturnUpdatedResponseDTO() {
        LancamentoRequestDTO updateRequest = new LancamentoRequestDTO(
                "Teatro", LocalDate.now(), null, new BigDecimal("80.00"),
                null, TipoLancamento.DESPESA, 1L, 1L
        );
        LancamentoResponseDTO updatedResponse = new LancamentoResponseDTO(
                1L, "Teatro", LocalDate.now(), null, new BigDecimal("80.00"),
                null, TipoLancamento.DESPESA, 1L, "Lazer", 1L, "João Silva"
        );

        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(lancamentoRepository.save(any())).thenReturn(lancamento);
        when(mapper.toDTO(any())).thenReturn(updatedResponse);

        LancamentoResponseDTO result = service.update(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.descricao()).isEqualTo("Teatro");
        assertThat(result.valor()).isEqualByComparingTo("80.00");
        verify(mapper).updateEntity(eq(lancamento), eq(updateRequest), eq(pessoa), eq(categoria));
        verify(lancamentoRepository).save(lancamento);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar lançamento com ID inexistente")
    void updateShouldThrowExceptionWhenLancamentoDoesNotExist() {
        when(lancamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lançamento não encontrado para o ID: 99");

        verify(lancamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar lançamento com pessoa inexistente")
    void updateShouldThrowExceptionWhenPessoaDoesNotExist() {
        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Pessoa não encontrada");

        verify(lancamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar lançamento com categoria inexistente")
    void updateShouldThrowExceptionWhenCategoriaDoesNotExist() {
        when(lancamentoRepository.findById(1L)).thenReturn(Optional.of(lancamento));
        when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(1L, requestDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Categoria não encontrada");

        verify(lancamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar lançamento com ID inexistente")
    void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        when(lancamentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Lançamento não encontrado para o ID: 99");

        verify(lancamentoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve retornar página de lançamentos filtrada")
    void findAllShouldReturnPageOfLancamentos() {
        Page<Lancamento> page = new PageImpl<>(List.of(lancamento), PageRequest.of(0, 10), 1);

        when(lancamentoRepository.findAll(
                ArgumentMatchers.<Specification<Lancamento>>any(), 
                any(Pageable.class)))
                .thenReturn(page);
        when(mapper.toDTO(lancamento)).thenReturn(responseDTO);

        Page<LancamentoResponseDTO> result = service.findAll(new LancamentoFilter(), PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).descricao()).isEqualTo("Cinema");
    }
}