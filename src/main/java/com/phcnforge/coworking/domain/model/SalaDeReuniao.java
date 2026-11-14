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
@Table(name = "salas_reuniao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaDeReuniao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false)
    private Integer andar;

    @Builder.Default
    @Column(name = "possui_projetor", nullable = false)
    private boolean possuiProjetor = false;

    @Embedded
    private Capacidade capacidade;

    @Embedded
    private Tarifa tarifa;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativa = true;
}
