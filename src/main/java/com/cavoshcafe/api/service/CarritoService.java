package com.cavoshcafe.api.service;

import com.cavoshcafe.api.dto.CarritoRequest;
import com.cavoshcafe.api.dto.CarritoResponse;
import com.cavoshcafe.api.model.Carrito;
import com.cavoshcafe.api.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Transactional
    public void agregarAlCarrito(Long usuarioId, CarritoRequest request) {
        carritoRepository.spAgregarAlCarrito(
                usuarioId,
                request.getProductoId(),
                request.getCantidad(),
                request.getNotas());
    }

    public List<CarritoResponse> obtenerCarritoUsuario(Long usuarioId) {
        List<Carrito> items = carritoRepository.findByUsuarioId(usuarioId);

        return items.stream().map(item -> {
            BigDecimal subtotal = item.getProducto().getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            return CarritoResponse.builder()
                    .carritoId(item.getId())
                    .productoId(item.getProducto().getId())
                    .productoNombre(item.getProducto().getNombre())
                    .precioUnitario(item.getProducto().getPrecio())
                    .cantidad(item.getCantidad())
                    .subtotal(subtotal)
                    .notas(item.getNotas())
                    .imagenUrl(item.getProducto().getImagenUrl())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        carritoRepository.deleteByUsuarioId(usuarioId);
    }
}