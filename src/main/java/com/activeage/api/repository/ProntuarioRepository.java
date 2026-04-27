package com.activeage.api.repository;

import com.activeage.api.model.Prontuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ProntuarioRepository extends MongoRepository<Prontuario, String> {
    List<Prontuario> findByPacienteIdOrderByDataRegistroDesc(String pacienteId);
}