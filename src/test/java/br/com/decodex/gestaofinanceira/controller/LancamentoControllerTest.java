package br.com.decodex.gestaofinanceira.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoRequestDTO;
import br.com.decodex.gestaofinanceira.dto.lancamento.LancamentoResponseDTO;
import br.com.decodex.gestaofinanceira.model.TipoLancamento;
import br.com.decodex.gestaofinanceira.service.LancamentoService;
import br.com.decodex.gestaofinanceira.storage.S3;

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

    // S3 é @Autowired no controller, precisa ser mockado no contexto do @WebMvcTest
    @MockitoBean
    private S3 s3;

    private final String BASE_URL = "/api/lancamentos";

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /save - Deve retornar 201 Created")
    void createShouldReturnCreated() throws Exception {
        LancamentoRequestDTO request = new LancamentoRequestDTO(
                "Salário", LocalDate.now(), null, new BigDecimal("5000.00"),
                null, TipoLancamento.RECEITA, 1L, 1L,
                null, null  // anexo, urlAnexo
        );
        LancamentoResponseDTO response = new LancamentoResponseDTO(
                1L, "Salário", LocalDate.now(), null, new BigDecimal("5000.00"),
                null, TipoLancamento.RECEITA, 1L, "Renda", 1L, "Tiago"
        );

        // O controller chama service.create(dto, null) — MultipartFile é null no endpoint /save
        when(lancamentoService.create(any(LancamentoRequestDTO.class), isNull()))
                .thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.descricao").value("Salário"))
                .andExpect(jsonPath("$.tipo").value("RECEITA"))
                .andExpect(jsonPath("$.categoriaNome").value("Renda"))
                .andExpect(jsonPath("$.pessoaNome").value("Tiago"));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /update/{id} - Deve retornar 200 OK")
    void updateShouldReturnOk() throws Exception {
        LancamentoRequestDTO request = new LancamentoRequestDTO(
                "Aluguel", LocalDate.now(), null, new BigDecimal("1200.00"),
                null, TipoLancamento.DESPESA, 2L, 1L,
                null, null  // anexo, urlAnexo
        );
        LancamentoResponseDTO response = new LancamentoResponseDTO(
                1L, "Aluguel", LocalDate.now(), null, new BigDecimal("1200.00"),
                null, TipoLancamento.DESPESA, 2L, "Moradia", 1L, "Tiago"
        );

        // O controller chama service.update(id, dto, null) — MultipartFile é null no endpoint /update/{id}
        when(lancamentoService.update(eq(1L), any(LancamentoRequestDTO.class), isNull()))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descricao").value("Aluguel"))
                .andExpect(jsonPath("$.tipo").value("DESPESA"))
                .andExpect(jsonPath("$.categoriaNome").value("Moradia"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /findById/{id} - Deve retornar 200 OK")
    void findByIdShouldReturnOk() throws Exception {
        LancamentoResponseDTO response = new LancamentoResponseDTO(
                1L, "Internet", LocalDate.now(), null, new BigDecimal("150.00"),
                null, TipoLancamento.DESPESA, 3L, "Utilidades", 1L, "Tiago"
        );

        when(lancamentoService.findByIdDTO(1L)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/findById/{id}", 1L))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.descricao").value("Internet"))
                .andExpect(jsonPath("$.categoriaNome").value("Utilidades"))
                .andExpect(jsonPath("$.pessoaNome").value("Tiago"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /listAll - Deve retornar 200 OK com página")
    void findAllShouldReturnPage() throws Exception {
        Page<LancamentoResponseDTO> page = new PageImpl<>(List.of(
                new LancamentoResponseDTO(
                        1L, "Salário", LocalDate.now(), null, new BigDecimal("5000.00"),
                        null, TipoLancamento.RECEITA, 1L, "Renda", 1L, "Tiago"
                )
        ), PageRequest.of(0, 10), 1);

        when(lancamentoService.findAll(any(), any())).thenReturn(page);

        mockMvc.perform(get(BASE_URL + "/listAll")
                .param("descricao", "Salário")
                .param("page", "0")
                .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].descricao").value("Salário"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /delete/{id} - Deve retornar 204 No Content")
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/delete/{id}", 1L)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}