package com.activeage.api.repository;

import com.activeage.api.model.Exame;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExameRepository extends MongoRepository<Exame, String> {
    List<Exame> findByPacienteIdOrderByDataUploadDesc(String pacienteId);
}