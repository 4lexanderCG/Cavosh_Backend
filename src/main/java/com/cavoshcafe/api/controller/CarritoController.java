package com.cavoshcafe.api.controller;

import com.cavoshcafe.api.dto.CarritoRequest;
import com.cavoshcafe.api.dto.CarritoResponse;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import com.cavoshcafe.api.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
@CrossOrigin(origins = "*")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long obtenerUsuarioId(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        return usuario.getId();
    }

    @GetMapping
    public ResponseEntity<List<CarritoResponse>> verCarrito(Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(carritoService.obtenerCarritoUsuario(usuarioId));
    }

    @PostMapping("/agregar")
    public ResponseEntity<Map<String, String>> agregarAlCarrito(
            Authentication authentication,
            @Valid @RequestBody CarritoRequest request) {
        Long usuarioId = obtenerUsuarioId(authentication);
        carritoService.agregarAlCarrito(usuarioId, request);
        return ResponseEntity.ok(Map.of("mensaje", "Producto agregado al carrito con éxito"));
    }

    @DeleteMapping("/vaciar")
    public ResponseEntity<Map<String, String>> vaciarCarrito(Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Carrito vaciado"));
    }
}