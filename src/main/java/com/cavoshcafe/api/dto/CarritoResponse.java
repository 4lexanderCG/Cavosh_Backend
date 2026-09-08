package com.cavoshcafe.api.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarritoResponse {
    private Long carritoId;
    private Long productoId;
    private String productoNombre;
    private BigDecimal precioUnitario;
    private Integer cantidad;
    private BigDecimal subtotal;
    private String notas;
    private String imagenUrl;
}
