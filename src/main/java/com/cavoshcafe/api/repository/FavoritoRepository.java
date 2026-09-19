package com.cavoshcafe.api.repository;

import com.cavoshcafe.api.model.Favorito;
import com.cavoshcafe.api.model.FavoritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {
    List<Favorito> findByUsuarioId(Long usuarioId);

    // Llamada a sp_gestionar_favorito (agrega o elimina)
    @Procedure(procedureName = "sp_gestionar_favorito")
    void spGestionarFavorito(
            @Param("p_usuario_id") Long usuarioId,
            @Param("p_producto_id") Long productoId);
}
