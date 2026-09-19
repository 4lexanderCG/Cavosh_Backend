package com.cavoshcafe.api.service;

import com.cavoshcafe.api.model.Favorito;
import com.cavoshcafe.api.repository.FavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    public List<Favorito> obtenerFavoritosUsuario(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void gestionarFavorito(Long usuarioId, Long productoId) {
        favoritoRepository.spGestionarFavorito(usuarioId, productoId);
    }
}
