package com.phcnforge.coworking.repository;

import com.phcnforge.coworking.domain.model.EstacaoDeTrabalho;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstacaoDeTrabalhoRepository extends JpaRepository<EstacaoDeTrabalho, Long> {

    boolean existsByCodigo(String codigo);

    Optional<EstacaoDeTrabalho> findByCodigo(String codigo);
}
