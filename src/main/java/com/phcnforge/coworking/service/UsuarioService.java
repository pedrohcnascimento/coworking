package com.phcnforge.coworking.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        Email email = new Email(dto.email());
        DocumentoIdentificacao documento = new DocumentoIdentificacao(dto.documento());

        if (usuarioRepository.existsByEmailEndereco(email.getEndereco())) {
            throw new ConflitoReservaException("Já existe um usuário cadastrado com este e-mail.");
        }
        if (usuarioRepository.existsByDocumentoNumero(documento.getNumero())) {
            throw new ConflitoReservaException("Já existe um usuário cadastrado com este documento.");
        }

        Usuario usuario = Usuario.builder()
                .nome(dto.nome().trim())
                .email(email)
                .documento(documento)
                .senha(passwordEncoder.encode(dto.senha()))
                .inadimplente(dto.inadimplente() != null ? dto.inadimplente() : false)
                .ativo(dto.ativo() != null ? dto.ativo() : true)
                .limiteHorasSemanais(dto.limiteHorasSemanais() != null ? dto.limiteHorasSemanais() : 40)
                .build();

        return UsuarioResponseDTO.deEntidade(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::deEntidade)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioResponseDTO.deEntidade(buscarEntidadePorId(id));
    }

    @Transactional
    public UsuarioResponseDTO atualizarCompleto(Long id, UsuarioRequestDTO dto) {
        Usuario usuario = buscarEntidadePorId(id);
        Email novoEmail = new Email(dto.email());
        DocumentoIdentificacao novoDocumento = new DocumentoIdentificacao(dto.documento());

        if (!usuario.getEmail().equals(novoEmail)
                && usuarioRepository.existsByEmailEndereco(novoEmail.getEndereco())) {
            throw new ConflitoReservaException("Já existe outro usuário cadastrado com este e-mail.");
        }
        if (!usuario.getDocumento().equals(novoDocumento)
                && usuarioRepository.existsByDocumentoNumero(novoDocumento.getNumero())) {
            throw new ConflitoReservaException("Já existe outro usuário cadastrado com este documento.");
        }

        usuario.setNome(dto.nome().trim());
        usuario.setEmail(novoEmail);
        usuario.setDocumento(novoDocumento);
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setInadimplente(dto.inadimplente() != null ? dto.inadimplente() : false);
        usuario.setAtivo(dto.ativo() != null ? dto.ativo() : true);
        usuario.setLimiteHorasSemanais(dto.limiteHorasSemanais() != null ? dto.limiteHorasSemanais() : 40);

        return UsuarioResponseDTO.deEntidade(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizarParcial(Long id, UsuarioPatchDTO dto) {
        Usuario usuario = buscarEntidadePorId(id);

        if (dto.nome() != null && !dto.nome().isBlank()) {
            usuario.setNome(dto.nome().trim());
        }
        if (dto.email() != null && !dto.email().isBlank()) {
            Email novoEmail = new Email(dto.email());
            if (!usuario.getEmail().equals(novoEmail)
                    && usuarioRepository.existsByEmailEndereco(novoEmail.getEndereco())) {
                throw new ConflitoReservaException("Já existe outro usuário cadastrado com este e-mail.");
            }
            usuario.setEmail(novoEmail);
        }
        if (dto.documento() != null && !dto.documento().isBlank()) {
            DocumentoIdentificacao novoDocumento = new DocumentoIdentificacao(dto.documento());
            if (!usuario.getDocumento().equals(novoDocumento)
                    && usuarioRepository.existsByDocumentoNumero(novoDocumento.getNumero())) {
                throw new ConflitoReservaException("Já existe outro usuário cadastrado com este documento.");
            }
            usuario.setDocumento(novoDocumento);
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }
        if (dto.inadimplente() != null) {
            usuario.setInadimplente(dto.inadimplente());
        }
        if (dto.ativo() != null) {
            usuario.setAtivo(dto.ativo());
        }
        if (dto.limiteHorasSemanais() != null) {
            usuario.setLimiteHorasSemanais(dto.limiteHorasSemanais());
        }

        return UsuarioResponseDTO.deEntidade(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deletar(Long id) {
        Usuario usuario = buscarEntidadePorId(id);
        usuarioRepository.delete(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado com ID: " + id));
    }
}
