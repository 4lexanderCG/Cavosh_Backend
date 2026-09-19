package com.cavoshcafe.api.repository;

import com.cavoshcafe.api.model.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUsuarioId(Long usuarioId);

    // Llamada a sp_agregar_direccion
    @Procedure(procedureName = "sp_agregar_direccion")
    void spAgregarDireccion(
            @Param("p_usuario_id") Long usuarioId,
            @Param("p_alias") String alias,
            @Param("p_direccion") String direccion,
            @Param("p_referencia") String referencia,
            @Param("p_principal") Boolean principal);
}
