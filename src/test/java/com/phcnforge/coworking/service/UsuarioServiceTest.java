package com.phcnforge.coworking.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.phcnforge.coworking.domain.model.Usuario;
import com.phcnforge.coworking.domain.vo.DocumentoIdentificacao;
import com.phcnforge.coworking.domain.vo.Email;
import com.phcnforge.coworking.dto.UsuarioPatchDTO;
import com.phcnforge.coworking.dto.request.UsuarioRequestDTO;
import com.phcnforge.coworking.dto.response.UsuarioResponseDTO;
import com.phcnforge.coworking.exception.ConflitoReservaException;
import com.phcnforge.coworking.exception.RecursoNaoEncontradoException;
import com.phcnforge.coworking.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPadrao;

    @BeforeEach
    void setUp() {
        usuarioPadrao = Usuario.builder()
                .id(1L)
                .nome("Carlos Mendes")
                .email(new Email("carlos@email.com"))
                .documento(new DocumentoIdentificacao("12345678901"))
                .senha("hashSenha")
                .inadimplente(false)
                .ativo(true)
                .limiteHorasSemanais(40)
                .build();
    }

    @Test
    @DisplayName("Deve criar usuário com sucesso")
    void deveCriarUsuarioComSucesso() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Carlos Mendes", "carlos@email.com", "12345678901", "senha123", false, true, 40);

        when(usuarioRepository.existsByEmailEndereco("carlos@email.com")).thenReturn(false);
        when(usuarioRepository.existsByDocumentoNumero("12345678901")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPadrao);

        UsuarioResponseDTO resposta = usuarioService.criar(dto);

        assertNotNull(resposta);
        assertEquals(1L, resposta.id());
        assertEquals("carlos@email.com", resposta.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar usuário com e-mail duplicado")
    void deveLancarExcecaoEmailDuplicado() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Carlos Mendes", "carlos@email.com", "12345678901", "senha123", false, true, 40);

        when(usuarioRepository.existsByEmailEndereco("carlos@email.com")).thenReturn(true);

        assertThrows(ConflitoReservaException.class, () -> usuarioService.criar(dto));
    }

    @Test
    @DisplayName("Deve listar e buscar usuário por ID")
    void deveListarEBuscarPorId() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPadrao));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));

        List<UsuarioResponseDTO> lista = usuarioService.listarTodos();
        UsuarioResponseDTO encontrado = usuarioService.buscarPorId(1L);

        assertEquals(1, lista.size());
        assertEquals("Carlos Mendes", encontrado.nome());
    }

    @Test
    @DisplayName("Deve atualizar usuário completamente via PUT")
    void deveAtualizarUsuarioCompleto() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO(
                "Carlos M. Atualizado", "carlos.novo@email.com", "12345678901", "novaSenha", false, true, 35);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
        when(usuarioRepository.existsByEmailEndereco("carlos.novo@email.com")).thenReturn(false);
        when(passwordEncoder.encode("novaSenha")).thenReturn("novoHash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO atualizado = usuarioService.atualizarCompleto(1L, dto);

        assertEquals("Carlos M. Atualizado", atualizado.nome());
        assertEquals("carlos.novo@email.com", atualizado.email());
        assertEquals(35, atualizado.limiteHorasSemanais());
    }

    @Test
    @DisplayName("Deve atualizar parcialmente o usuário via PATCH")
    void deveAtualizarUsuarioParcial() {
        UsuarioPatchDTO patch = new UsuarioPatchDTO(null, null, null, null, true, false, 20);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponseDTO atualizado = usuarioService.atualizarParcial(1L, patch);

        assertTrue(atualizado.inadimplente());
        assertFalse(atualizado.ativo());
        assertEquals(20, atualizado.limiteHorasSemanais());
    }

    @Test
    @DisplayName("Deve deletar usuário existente e lançar erro quando não encontrado")
    void deveDeletarUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPadrao));
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        usuarioService.deletar(1L);
        verify(usuarioRepository).delete(usuarioPadrao);

        assertThrows(RecursoNaoEncontradoException.class, () -> usuarioService.deletar(99L));
    }
}
