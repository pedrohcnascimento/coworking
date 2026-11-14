package com.phcnforge.coworking.controller;

import com.phcnforge.coworking.dto.request.ReservaRequestDTO;
import com.phcnforge.coworking.dto.response.ReservaResponseDTO;
import com.phcnforge.coworking.service.ReservaService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas (Desk Booking)", description = "Agendamento e controle de vagas de coworking")
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    @Operation(
            summary = "Realizar nova reserva",
            description = "Valida conflito de horário, inadimplência do membro e limite de horas semanais antes de confirmar.")
    public ResponseEntity<ReservaResponseDTO> criar(@Valid @RequestBody ReservaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criarReserva(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todas as reservas")
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(reservaService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva por ID")
    public ResponseEntity<ReservaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar reservas por usuário")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(reservaService.listarPorUsuario(usuarioId));
    }

    @PatchMapping("/{id}/cancelar")
    @Operation(
            summary = "Cancelar reserva",
            description = "Cancela uma reserva ativa respeitando a antecedência mínima de 2 horas.")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(reservaService.cancelarReserva(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir registro de reserva")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
