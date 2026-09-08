package com.cavoshcafe.api.controller;

import com.cavoshcafe.api.dto.ActualizarEstadoPedidoRequest;
import com.cavoshcafe.api.dto.CrearPedidoRequest;
import com.cavoshcafe.api.model.Pedido;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import com.cavoshcafe.api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long obtenerUsuarioId(Authentication auth) {
        Usuario usuario = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
        return usuario.getId();
    }

    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearPedido(
            Authentication authentication,
            @Valid @RequestBody CrearPedidoRequest request) {
        Long usuarioId = obtenerUsuarioId(authentication);
        Long pedidoId = pedidoService.crearPedido(usuarioId, request);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Pedido creado con éxito",
                "pedidoId", pedidoId));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Pedido>> obtenerHistorial(Authentication authentication) {
        Long usuarioId = obtenerUsuarioId(authentication);
        return ResponseEntity.ok(pedidoService.obtenerHistorialUsuario(usuarioId));
    }

    @GetMapping("/{pedidoId}")
    public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(pedidoId));
    }

    @PutMapping("/{pedidoId}/estado")
    public ResponseEntity<Map<String, String>> actualizarEstado(
            @PathVariable Long pedidoId,
            @Valid @RequestBody ActualizarEstadoPedidoRequest request) {
        pedidoService.actualizarEstado(pedidoId, request.getNuevoEstado());
        return ResponseEntity.ok(Map.of("mensaje", "Estado del pedido actualizado"));
    }
}