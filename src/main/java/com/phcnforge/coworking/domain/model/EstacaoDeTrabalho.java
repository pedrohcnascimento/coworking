package com.phcnforge.coworking.domain.model;

import com.phcnforge.coworking.domain.vo.Capacidade;
import com.phcnforge.coworking.domain.vo.Tarifa;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estacoes_trabalho")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstacaoDeTrabalho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String descricao;

    @Embedded
    private Capacidade capacidade;

    @Embedded
    private Tarifa tarifa;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativa = true;
}
