package com.cavoshcafe.api.controller;

import com.cavoshcafe.api.dto.ActualizarPerfilRequest;
import com.cavoshcafe.api.dto.PerfilUsuarioResponse;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import com.cavoshcafe.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long obtenerUsuarioId(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        return usuario.getId();
    }

    @GetMapping("/perfil")
    public ResponseEntity<PerfilUsuarioResponse> obtenerPerfil(Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(usuarioService.obtenerPerfil(usuarioId));
    }

    @PutMapping("/perfil")
    public ResponseEntity<Map<String, String>> actualizarPerfil(
            Authentication authentication,
            @Valid @RequestBody ActualizarPerfilRequest request) {
        Long usuarioId = obtenerUsuarioId(authentication);
        usuarioService.actualizarPerfil(usuarioId, request);
        return ResponseEntity.ok(Map.of("mensaje", "Perfil actualizado con éxito"));
    }
}
