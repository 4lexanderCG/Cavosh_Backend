package com.cavoshcafe.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CrearPedidoRequest {
    private Long direccionId;

    @NotBlank(message = "El tipo de entrega es obligatorio (delivery, para_llevar, en_local)")
    private String tipoEntrega;

    @NotBlank(message = "El método de pago es obligatorio (efectivo, tarjeta, billetera_digital)")
    private String metodoPago;

    private BigDecimal costoEnvio = BigDecimal.ZERO;
}
