package com.phcnforge.coworking.repository;

import com.phcnforge.coworking.domain.model.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailEndereco(String endereco);

    boolean existsByDocumentoNumero(String numero);
}
