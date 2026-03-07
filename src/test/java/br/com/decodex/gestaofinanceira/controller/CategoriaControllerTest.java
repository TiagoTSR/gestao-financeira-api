package br.com.decodex.gestaofinanceira.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import br.com.decodex.gestaofinanceira.dto.CategoriaRequestDTO;
import br.com.decodex.gestaofinanceira.dto.CategoriaResponseDTO;
import br.com.decodex.gestaofinanceira.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Removemos o @Autowired daqui
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoriaService service;
    
    @MockitoBean
    private JwtServiceGenerator jwtService;
    
    @MockitoBean
    private UserDetailsService userDetailsService;

    private final String BASE_URL = "/api/categorias";

    @BeforeEach
    void setUp() {
        // Inicializamos manualmente para garantir que ele exista, 
        // independentemente das falhas de autoconfiguração do Spring
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /save - Deve retornar 201 ao criar categoria válida")
    void createShouldReturnCreated() throws Exception {
        CategoriaRequestDTO request = new CategoriaRequestDTO("Alimentação");
        CategoriaResponseDTO response = new CategoriaResponseDTO(1L, "Alimentação");

        when(service.create(any(CategoriaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/save")
                .with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nome").value("Alimentação")); // Corrigi o "Alimentação1" para passar
    }

    @Test
    @WithMockUser
    @DisplayName("GET /findById/{id} - Deve retornar 200 ao encontrar categoria")
    void findByIdShouldReturnOk() throws Exception {
        CategoriaResponseDTO response = new CategoriaResponseDTO(1L, "Lazer");
        
        when(service.findByIdDTO(1L)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/findById/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Lazer"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /listAllSimple - Deve retornar lista de categorias")
    void listAllSimpleShouldReturnList() throws Exception {
        List<CategoriaResponseDTO> list = List.of(new CategoriaResponseDTO(1L, "Saúde"));
        
        when(service.findAllSimple()).thenReturn(list);

        mockMvc.perform(get(BASE_URL + "/listAllSimple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Saúde"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /listAll - Deve retornar página de categorias")
    void listAllShouldReturnPage() throws Exception {
        var response = new CategoriaResponseDTO(1L, "Educação");
        var page = new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1);
        
        when(service.findAll(any(), any())).thenReturn(page);

        mockMvc.perform(get(BASE_URL + "/listAll")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nome").value("Educação"));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /delete/{id} - Deve retornar 204")
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/delete/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}