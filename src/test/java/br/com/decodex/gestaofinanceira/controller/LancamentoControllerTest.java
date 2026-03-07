package br.com.decodex.gestaofinanceira.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.decodex.gestaofinanceira.auth.JwtServiceGenerator;
import br.com.decodex.gestaofinanceira.dto.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;
import br.com.decodex.gestaofinanceira.service.LancamentoService;

@WebMvcTest(LancamentoController.class)
class LancamentoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private LancamentoService lancamentoService;

    @MockitoBean
    private JwtServiceGenerator jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final String BASE_URL = "/api/lancamentos";

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        // Essencial para lidar com LocalDate nos Records
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /save - Deve criar um lançamento de RECEITA")
    void createShouldReturnCreated() throws Exception {
        LancamentoRequestDTO request = new LancamentoRequestDTO(
                "Salário Mensal", 
                LocalDate.of(2026, 3, 7), 
                null, 
                new BigDecimal("5000.00"), 
                "Bônus incluído", 
                TipoLancamento.RECEITA, 
                1L, 
                1L
        );

        LancamentoResponseDTO response = new LancamentoResponseDTO(
                100L, "Salário Mensal", LocalDate.of(2026, 3, 7), null, 
                new BigDecimal("5000.00"), "Bônus incluído", TipoLancamento.RECEITA, 
                1L, "Renda", 1L, "João Silva"
        );

        when(lancamentoService.create(any(LancamentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.descricao").value("Salário Mensal"))
                .andExpect(jsonPath("$.tipo").value("RECEITA"))
                .andExpect(jsonPath("$.valor").value(5000.00));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /update/{id} - Deve atualizar lançamento")
    void updateShouldReturnOk() throws Exception {
        LancamentoRequestDTO request = new LancamentoRequestDTO(
                "Aluguel Alterado", LocalDate.now(), null, 
                new BigDecimal("1200.00"), null, TipoLancamento.DESPESA, 2L, 1L
        );
        
        LancamentoResponseDTO response = new LancamentoResponseDTO(
                1L, "Aluguel Alterado", LocalDate.now(), null, 
                new BigDecimal("1200.00"), null, TipoLancamento.DESPESA, 
                2L, "Moradia", 1L, "João Silva"
        );

        when(lancamentoService.update(eq(1L), any(LancamentoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Aluguel Alterado"))
                .andExpect(jsonPath("$.valor").value(1200.00));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /findById/{id} - Deve retornar lançamento com nomes de categoria e pessoa")
    void findByIdShouldReturnOk() throws Exception {
        LancamentoResponseDTO response = new LancamentoResponseDTO(
                1L, "Internet", LocalDate.now(), LocalDate.now(), 
                new BigDecimal("150.00"), null, TipoLancamento.DESPESA, 
                3L, "Utilidades", 1L, "João Silva"
        );

        when(lancamentoService.findByIdDTO(1L)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/findById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoriaNome").value("Utilidades"))
                .andExpect(jsonPath("$.pessoaNome").value("João Silva"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /listAll - Deve validar filtros e retornar página")
    void findAllShouldReturnPage() throws Exception {
    	Page<LancamentoResponseDTO> page = new PageImpl<>(
                List.of(), 
                PageRequest.of(0, 10), 
                0
        );

        when(lancamentoService.findAll(any(), any())).thenReturn(page);

        // Testando com os filtros que o QueryParamValidator exige
        mockMvc.perform(get(BASE_URL + "/listAll")
                .param("descricao", "Aluguel")
                .param("dataVencimentoDe", "2026-01-01")
                .param("dataVencimentoAte", "2026-12-31")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /delete/{id} - Deve retornar 204")
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/delete/{id}", 1L)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}