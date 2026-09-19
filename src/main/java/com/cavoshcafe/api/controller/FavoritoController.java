package com.cavoshcafe.api.controller;

import com.cavoshcafe.api.model.Favorito;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import com.cavoshcafe.api.service.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
@CrossOrigin(origins = "*")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long obtenerUsuarioId(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        return usuario.getId();
    }

    @GetMapping
    public ResponseEntity<List<Favorito>> listarFavoritos(Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(favoritoService.obtenerFavoritosUsuario(usuarioId));
    }

    @PostMapping("/{productoId}")
    public ResponseEntity<Map<String, String>> gestionarFavorito(
            Authentication authentication,
            @PathVariable Long productoId) {
        Long usuarioId = obtenerUsuarioId(authentication);
        favoritoService.gestionarFavorito(usuarioId, productoId);
        return ResponseEntity.ok(Map.of("mensaje", "Favorito actualizado con éxito"));
    }
}
