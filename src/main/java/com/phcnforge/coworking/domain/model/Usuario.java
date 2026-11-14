package com.phcnforge.coworking.domain.model;

import com.phcnforge.coworking.domain.vo.DocumentoIdentificacao;
import com.phcnforge.coworking.domain.vo.Email;
import com.phcnforge.coworking.exception.RegraNegocioException;
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
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Embedded
    private Email email;

    @Embedded
    private DocumentoIdentificacao documento;

    @Column(nullable = false)
    private String senha;

    @Builder.Default
    @Column(nullable = false)
    private boolean inadimplente = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @Builder.Default
    @Column(name = "limite_horas_semanais", nullable = false)
    private Integer limiteHorasSemanais = 40;

    public void validarAptidaoParaReserva(long horasReservadasNaSemana, long novasHoras) {
        if (!this.ativo) {
            throw new RegraNegocioException("Usuário inativo não pode realizar agendamentos.");
        }
        if (this.inadimplente) {
            throw new RegraNegocioException("Usuário bloqueado automaticamente por inadimplência.");
        }
        int limite = this.limiteHorasSemanais != null ? this.limiteHorasSemanais : 40;
        if (horasReservadasNaSemana + novasHoras > limite) {
            throw new RegraNegocioException(
                    String.format(
                            "Limite semanal de %d horas excedido. Horas já reservadas: %d, solicitadas: %d.",
                            limite, horasReservadasNaSemana, novasHoras));
        }
    }
}
