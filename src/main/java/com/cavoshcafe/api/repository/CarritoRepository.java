package com.cavoshcafe.api.repository;

import com.cavoshcafe.api.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    List<Carrito> findByUsuarioId(Long usuarioId);

    void deleteByUsuarioId(Long usuarioId);

    // Llamada a sp_agregar_al_carrito
    @Procedure(procedureName = "sp_agregar_al_carrito")
    void spAgregarAlCarrito(
            @Param("p_usuario_id") Long usuarioId,
            @Param("p_producto_id") Long productoId,
            @Param("p_cantidad") Integer cantidad,
            @Param("p_notas") String notas);
}
