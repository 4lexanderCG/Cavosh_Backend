package com.cavoshcafe.api.repository;

import org.springframework.stereotype.Repository;
import com.cavoshcafe.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    // Llamada a sp_registrar_usuario con outputParameterName
    @Procedure(procedureName = "sp_registrar_usuario", outputParameterName = "p_usuario_id")
    Long spRegistrarUsuario(
            @Param("p_nombre") String nombre,
            @Param("p_email") String email,
            @Param("p_telefono") String telefono,
            @Param("p_password") String password);

    // Llamada a sp_recuperar_password
    @Procedure(procedureName = "sp_recuperar_password")
    void spRecuperarPassword(
            @Param("p_email") String email,
            @Param("p_nuevo_password") String nuevoPassword);

    // Llamada a sp_actualizar_perfil
    @Procedure(procedureName = "sp_actualizar_perfil")
    void spActualizarPerfil(
            @Param("p_usuario_id") Long usuarioId,
            @Param("p_nombre") String nombre,
            @Param("p_telefono") String telefono);
}
