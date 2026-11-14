package com.phcnforge.coworking.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.phcnforge.coworking.dto.UsuarioPatchDTO;
import com.phcnforge.coworking.dto.request.UsuarioRequestDTO;
import com.phcnforge.coworking.dto.response.UsuarioResponseDTO;
import com.phcnforge.coworking.exception.GlobalExceptionHandler;
import com.phcnforge.coworking.exception.RecursoNaoEncontradoException;
import com.phcnforge.coworking.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/usuarios deve retornar 201 Created")
    void deveCriarUsuarioEndpoint() throws Exception {
        UsuarioResponseDTO response = new UsuarioResponseDTO(
                1L, "Ana Silva", "ana@email.com", "12345678909", false, true, 40);
        when(usuarioService.criar(any(UsuarioRequestDTO.class))).thenReturn(response);

        String json = """
                {
                  "nome": "Ana Silva",
                  "email": "ana@email.com",
                  "documento": "12345678909",
                  "senha": "password123",
                  "inadimplente": false,
                  "ativo": true,
                  "limiteHorasSemanais": 40
                }
                """;

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Ana Silva"));
    }

    @Test
    @DisplayName("GET /api/usuarios e GET /api/usuarios/{id} devem retornar 200 OK")
    void deveListarEBuscarUsuarioEndpoint() throws Exception {
        UsuarioResponseDTO response = new UsuarioResponseDTO(
                1L, "Ana Silva", "ana@email.com", "12345678909", false, true, 40);
        when(usuarioService.listarTodos()).thenReturn(List.of(response));
        when(usuarioService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("ana@email.com"));

        mockMvc.perform(get("/api/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.documento").value("12345678909"));
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} e PATCH /api/usuarios/{id} devem retornar 200 OK")
    void deveAtualizarPutEPatchEndpoint() throws Exception {
        UsuarioResponseDTO responsePut = new UsuarioResponseDTO(
                1L, "Ana Atualizada", "ana@email.com", "12345678909", false, true, 30);
        when(usuarioService.atualizarCompleto(eq(1L), any(UsuarioRequestDTO.class)))
                .thenReturn(responsePut);

        String putJson = """
                {
                  "nome": "Ana Atualizada",
                  "email": "ana@email.com",
                  "documento": "12345678909",
                  "senha": "password123",
                  "inadimplente": false,
                  "ativo": true,
                  "limiteHorasSemanais": 30
                }
                """;

        mockMvc.perform(put("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(putJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Atualizada"));

        UsuarioResponseDTO responsePatch = new UsuarioResponseDTO(
                1L, "Ana Atualizada", "ana@email.com", "12345678909", true, true, 20);
        when(usuarioService.atualizarParcial(eq(1L), any(UsuarioPatchDTO.class)))
                .thenReturn(responsePatch);

        String patchJson = """
                {
                  "inadimplente": true,
                  "limiteHorasSemanais": 20
                }
                """;

        mockMvc.perform(patch("/api/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inadimplente").value(true))
                .andExpect(jsonPath("$.limiteHorasSemanais").value(20));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} deve retornar 204 No Content e 404 quando inexistente")
    void deveDeletarETestar404Endpoint() throws Exception {
        doNothing().when(usuarioService).deletar(1L);
        when(usuarioService.buscarPorId(99L))
                .thenThrow(new RecursoNaoEncontradoException("Usuário não encontrado com ID: 99"));

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
