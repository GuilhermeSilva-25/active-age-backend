package com.activeage.api.controller;

import com.activeage.api.dto.ProntuarioRequestDTO;
import com.activeage.api.model.Prontuario;
import com.activeage.api.service.ProntuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prontuarios")
@RequiredArgsConstructor
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    @PostMapping("/medico/{medicoId}")
    public ResponseEntity<Prontuario> registrar(@PathVariable String medicoId, @RequestBody ProntuarioRequestDTO dto) {
        return ResponseEntity.ok(prontuarioService.registrarProntuario(medicoId, dto));
    }
}