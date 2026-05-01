package com.activeage.api.controller;

import com.activeage.api.model.Exame;
import com.activeage.api.repository.ExameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/exames")
@RequiredArgsConstructor
public class ExameController {

    private final ExameRepository exameRepository;

    @PostMapping("/paciente/{pacienteId}")
    public ResponseEntity<Exame> enviarExame(@PathVariable String pacienteId, @RequestBody Exame exame) {
        exame.setPacienteId(pacienteId);
        exame.setDataUpload(LocalDateTime.now());
        return ResponseEntity.ok(exameRepository.save(exame));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<Exame>> listarPorPaciente(@PathVariable String pacienteId) {
        return ResponseEntity.ok(exameRepository.findByPacienteIdOrderByDataUploadDesc(pacienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarExame(@PathVariable String id) {
        exameRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}