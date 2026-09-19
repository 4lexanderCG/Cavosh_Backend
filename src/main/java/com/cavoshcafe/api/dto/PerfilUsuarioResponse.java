package com.cavoshcafe.api.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerfilUsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private Integer puntosAcumulados;
    private Boolean notificacionesActivas;
    private LocalDateTime creadoEn;
}
