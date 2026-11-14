package com.phcnforge.coworking.controller;

import com.phcnforge.coworking.dto.UsuarioPatchDTO;
import com.phcnforge.coworking.dto.request.UsuarioRequestDTO;
import com.phcnforge.coworking.dto.response.UsuarioResponseDTO;
import com.phcnforge.coworking.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento completo (CRUD) de membros e usuários do coworking")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Cadastrar novo usuário", description = "Cria um novo membro no sistema com validação de e-mail e CPF/CNPJ.")
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna a lista de todos os usuários cadastrados.")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna os detalhes de um usuário específico.")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário completamente (PUT)", description = "Substitui todos os dados cadastrais do usuário.")
    public ResponseEntity<UsuarioResponseDTO> atualizarCompleto(
            @PathVariable Long id, @Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarCompleto(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar usuário parcialmente (PATCH)", description = "Atualiza apenas os campos informados (ex.: status de inadimplência, limite semanal de horas, e-mail ou nome).")
    public ResponseEntity<UsuarioResponseDTO> atualizarParcial(
            @PathVariable Long id, @Valid @RequestBody UsuarioPatchDTO dto) {
        return ResponseEntity.ok(usuarioService.atualizarParcial(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover usuário", description = "Exclui permanentemente um usuário pelo ID.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
