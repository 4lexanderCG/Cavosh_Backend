package com.cavoshcafe.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DireccionRequest {
    @NotBlank(message = "El alias es obligatorio (ej. Casa, Oficina)")
    private String alias;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    private String referencia;

    private Boolean esPrincipal = false;
}
