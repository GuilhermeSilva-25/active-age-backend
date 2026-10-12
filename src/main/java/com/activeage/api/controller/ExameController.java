package com.activeage.api.controller;

import com.activeage.api.model.Exame;
import com.activeage.api.repository.ExameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/exames")
@RequiredArgsConstructor
public class ExameController {

    private final ExameRepository exameRepository;

    @PostMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<Exame> enviarExame(@PathVariable String pacienteId, @RequestBody Exame exame) {
        exame.setPacienteId(pacienteId);
        exame.setDataUpload(LocalDateTime.now());
        return ResponseEntity.ok(exameRepository.save(exame));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'MEDICO', 'ADMIN')")
    public ResponseEntity<List<Exame>> listarPorPaciente(@PathVariable String pacienteId) {
        return ResponseEntity.ok(exameRepository.findByPacienteIdOrderByDataUploadDesc(pacienteId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'ADMIN')")
    public ResponseEntity<Void> deletarExame(@PathVariable String id) {
        exameRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}