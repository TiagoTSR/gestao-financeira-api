package br.com.decodex.gestaofinanceira.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        verify(lancamentoRepository, times(1)).delete(lancamento);
    }
}