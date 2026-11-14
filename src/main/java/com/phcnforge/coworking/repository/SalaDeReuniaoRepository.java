package com.phcnforge.coworking.repository;

import com.phcnforge.coworking.domain.model.SalaDeReuniao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaDeReuniaoRepository extends JpaRepository<SalaDeReuniao, Long> {

    boolean existsByNomeIgnoreCase(String nome);
}
