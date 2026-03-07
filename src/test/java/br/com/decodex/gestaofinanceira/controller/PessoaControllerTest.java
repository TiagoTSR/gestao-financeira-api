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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
import br.com.decodex.gestaofinanceira.dto.EnderecoDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.PessoaResponseDTO;
import br.com.decodex.gestaofinanceira.service.PessoaService;

@WebMvcTest(PessoaController.class)
class PessoaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private PessoaService pessoaService;

    @MockitoBean
    private JwtServiceGenerator jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private final String BASE_URL = "/api/pessoas";

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /save - Deve salvar pessoa com endereço completo")
    void createShouldReturnCreated() throws Exception {
        // Criando o endereço com todos os campos do seu record
        EnderecoDTO endereco = new EnderecoDTO(
                "Rua das Palmeiras", "100", "Apto 201", 
                "Jardim", "12345-678", "São Paulo", "SP"
        );
        
        PessoaRequestDTO request = new PessoaRequestDTO("Carlos Alberto", endereco, true);
        PessoaResponseDTO response = new PessoaResponseDTO(1L, "Carlos Alberto", endereco, true);

        when(pessoaService.create(any(PessoaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Carlos Alberto"))
                .andExpect(jsonPath("$.endereco.logradouro").value("Rua das Palmeiras"))
                .andExpect(jsonPath("$.endereco.cidade").value("São Paulo"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /update/{id} - Deve atualizar dados e endereço")
    void updateShouldReturnOk() throws Exception {
        EnderecoDTO enderecoAtualizado = new EnderecoDTO(
                "Av. Paulista", "1000", null, 
                "Bela Vista", "01310-100", "São Paulo", "SP"
        );
        
        PessoaRequestDTO request = new PessoaRequestDTO("Carlos Silva", enderecoAtualizado, false);
        PessoaResponseDTO response = new PessoaResponseDTO(1L, "Carlos Silva", enderecoAtualizado, false);

        when(pessoaService.update(eq(1L), any(PessoaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Silva"))
                .andExpect(jsonPath("$.endereco.logradouro").value("Av. Paulista"))
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /findById/{id} - Deve retornar pessoa e endereço")
    void findByIdShouldReturnOk() throws Exception {
        EnderecoDTO endereco = new EnderecoDTO("Rua A", "1", null, "Centro", "00000-000", "BH", "MG");
        PessoaResponseDTO response = new PessoaResponseDTO(1L, "Ana Souza", endereco, true);
        
        when(pessoaService.findByIdDTO(1L)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/findById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.endereco.bairro").value("Centro"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /listAll - Deve retornar página com sucesso")
    void findAllShouldReturnPage() throws Exception {
        var page = new PageImpl<>(List.of(
                new PessoaResponseDTO(1L, "Pessoa Teste", null, true)
        ), PageRequest.of(0, 10), 1);

        when(pessoaService.findAll(any(), any())).thenReturn(page);

        mockMvc.perform(get(BASE_URL + "/listAll")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Pessoa Teste"));
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