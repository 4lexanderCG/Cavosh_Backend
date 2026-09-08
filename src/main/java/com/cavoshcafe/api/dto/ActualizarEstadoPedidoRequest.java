package com.cavoshcafe.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActualizarEstadoPedidoRequest {
    @NotBlank(message = "El nuevo estado es obligatorio")
    private String nuevoEstado;
}
