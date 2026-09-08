package com.cavoshcafe.api.repository;

import com.cavoshcafe.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioIdOrderByCreadoEnDesc(Long usuarioId);

    // Llamada a sp_crear_pedido con outputParameterName
    @Procedure(procedureName = "sp_crear_pedido", outputParameterName = "p_pedido_id")
    Long spCrearPedido(
            @Param("p_usuario_id") Long usuarioId,
            @Param("p_direccion_id") Long direccionId,
            @Param("p_tipo_entrega") String tipoEntrega,
            @Param("p_metodo_pago") String metodoPago,
            @Param("p_costo_envio") BigDecimal costoEnvio);

    // Llamada a sp_actualizar_estado_pedido
    @Procedure(procedureName = "sp_actualizar_estado_pedido")
    void spActualizarEstadoPedido(
            @Param("p_pedido_id") Long pedidoId,
            @Param("p_nuevo_estado") String nuevoEstado);
}
