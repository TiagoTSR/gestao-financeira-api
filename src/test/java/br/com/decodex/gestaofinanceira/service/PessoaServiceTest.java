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

import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
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

        verify(repository, times(1)).delete(pessoa);
    }
}
