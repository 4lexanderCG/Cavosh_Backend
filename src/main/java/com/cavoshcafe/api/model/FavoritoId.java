package com.cavoshcafe.api.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FavoritoId implements Serializable {
    private Long usuarioId;
    private Long productoId;
}
