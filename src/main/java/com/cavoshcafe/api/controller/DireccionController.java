package com.cavoshcafe.api.controller;

import com.cavoshcafe.api.dto.DireccionRequest;
import com.cavoshcafe.api.model.Direccion;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import com.cavoshcafe.api.service.DireccionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/direcciones")
@CrossOrigin(origins = "*")
public class DireccionController {

    @Autowired
    private DireccionService direccionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long obtenerUsuarioId(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        return usuario.getId();
    }

    @GetMapping
    public ResponseEntity<List<Direccion>> listarDirecciones(Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(direccionService.obtenerDireccionesUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> agregarDireccion(
            Authentication authentication,
            @Valid @RequestBody DireccionRequest request) {
        Long usuarioId = obtenerUsuarioId(authentication);
        direccionService.agregarDireccion(usuarioId, request);
        return ResponseEntity.ok(Map.of("mensaje", "Dirección registrada con éxito"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarDireccion(@PathVariable Long id) {
        direccionService.eliminarDireccion(id);
        return ResponseEntity.ok(Map.of("mensaje", "Dirección eliminada con éxito"));
    }
}
