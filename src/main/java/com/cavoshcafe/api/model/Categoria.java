package com.cavoshcafe.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoCategoria tipo = TipoCategoria.Bebidas;

    @Column(nullable = false)
    private Boolean activo = true;

    public enum TipoCategoria {
        Bebidas, Comida
    }
}
