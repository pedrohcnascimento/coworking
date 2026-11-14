package com.phcnforge.coworking.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.phcnforge.coworking.domain.vo.Capacidade;
import com.phcnforge.coworking.domain.vo.DocumentoIdentificacao;
import com.phcnforge.coworking.domain.vo.Email;
import com.phcnforge.coworking.domain.vo.IntervaloDeTempo;
import com.phcnforge.coworking.domain.vo.Tarifa;
import com.phcnforge.coworking.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ValueObjectsTest {

    @Test
    @DisplayName("Deve criar IntervaloDeTempo válido e calcular horas")
    void deveCriarIntervaloDeTempoValido() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 16, 9, 0);
        LocalDateTime fim = LocalDateTime.of(2026, 11, 16, 12, 0);

        IntervaloDeTempo intervalo = new IntervaloDeTempo(inicio, fim);

        assertEquals(3L, intervalo.duracaoEmHoras());
    }

    @Test
    @DisplayName("Deve rejeitar IntervaloDeTempo com duração menor que 1 hora ou maior que 8 horas")
    void deveRejeitarIntervaloComDuracaoInvalida() {
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 16, 9, 0);

        assertThrows(
                RegraNegocioException.class,
                () -> new IntervaloDeTempo(inicio, inicio.plusMinutes(30)));
        assertThrows(
                RegraNegocioException.class,
                () -> new IntervaloDeTempo(inicio, inicio.plusHours(9)));
    }

    @Test
    @DisplayName("Deve detectar sobreposição entre dois intervalos de tempo")
    void deveDetectarSobreposicaoDeIntervalos() {
        IntervaloDeTempo manha = new IntervaloDeTempo(
                LocalDateTime.of(2026, 11, 16, 9, 0),
                LocalDateTime.of(2026, 11, 16, 12, 0));
        IntervaloDeTempo conflito = new IntervaloDeTempo(
                LocalDateTime.of(2026, 11, 16, 11, 0),
                LocalDateTime.of(2026, 11, 16, 14, 0));
        IntervaloDeTempo semConflito = new IntervaloDeTempo(
                LocalDateTime.of(2026, 11, 16, 12, 0),
                LocalDateTime.of(2026, 11, 16, 15, 0));

        assertTrue(manha.sobrepoe(conflito));
        assertFalse(manha.sobrepoe(semConflito));
    }

    @Test
    @DisplayName("Deve validar Email e DocumentoIdentificacao corretamente")
    void deveValidarEmailEDocumento() {
        Email email = new Email("  Teste@Empresa.com ");
        assertEquals("teste@empresa.com", email.getEndereco());
        assertThrows(RegraNegocioException.class, () -> new Email("email-invalido"));

        DocumentoIdentificacao cpf = new DocumentoIdentificacao("123.456.789-09");
        assertEquals("12345678909", cpf.getNumero());
        assertThrows(RegraNegocioException.class, () -> new DocumentoIdentificacao("12345"));
    }

    @Test
    @DisplayName("Deve validar Tarifa e Capacidade")
    void deveValidarTarifaECapacidade() {
        Tarifa tarifa = new Tarifa(new BigDecimal("30.00"));
        assertEquals(new BigDecimal("90.00"), tarifa.calcularTotal(3));
        assertThrows(RegraNegocioException.class, () -> new Tarifa(BigDecimal.ZERO));

        Capacidade capacidade = new Capacidade(4);
        assertEquals(4, capacidade.getMaximoPessoas());
        assertThrows(RegraNegocioException.class, () -> new Capacidade(0));
    }
}
