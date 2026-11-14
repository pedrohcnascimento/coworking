package com.phcnforge.coworking.dto.response;

import com.phcnforge.coworking.domain.model.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String documento,
        boolean inadimplente,
        boolean ativo,
        Integer limiteHorasSemanais) {

    public static UsuarioResponseDTO deEntidade(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail() != null ? usuario.getEmail().getEndereco() : null,
                usuario.getDocumento() != null ? usuario.getDocumento().getNumero() : null,
                usuario.isInadimplente(),
                usuario.isAtivo(),
                usuario.getLimiteHorasSemanais());
    }
}
