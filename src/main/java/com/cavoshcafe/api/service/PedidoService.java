package com.cavoshcafe.api.service;

import com.cavoshcafe.api.dto.CrearPedidoRequest;
import com.cavoshcafe.api.model.Pedido;
import com.cavoshcafe.api.repository.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Transactional
    public Long crearPedido(Long usuarioId, CrearPedidoRequest request) {
        return pedidoRepository.spCrearPedido(
                usuarioId,
                request.getDireccionId(),
                request.getTipoEntrega(),
                request.getMetodoPago(),
                request.getCostoEnvio());
    }

    public List<Pedido> obtenerHistorialUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId);
    }

    public Pedido obtenerPorId(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }

    @Transactional
    public void actualizarEstado(Long pedidoId, String nuevoEstado) {
        pedidoRepository.spActualizarEstadoPedido(pedidoId, nuevoEstado);
    }
}