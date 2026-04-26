package com.activeage.api.repository;

import com.activeage.api.enums.StatusValidacao;
import com.activeage.api.enums.TipoUsuario;
import com.activeage.api.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByTipoAndStatusValidacao(TipoUsuario tipo, StatusValidacao status);
}