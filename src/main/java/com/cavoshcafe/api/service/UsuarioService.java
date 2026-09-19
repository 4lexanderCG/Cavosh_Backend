package com.cavoshcafe.api.service;

import com.cavoshcafe.api.dto.ActualizarPerfilRequest;
import com.cavoshcafe.api.dto.PerfilUsuarioResponse;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public PerfilUsuarioResponse obtenerPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        return PerfilUsuarioResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .rol(usuario.getRol().getNombre())
                .puntosAcumulados(usuario.getPuntosAcumulados())
                .notificacionesActivas(usuario.getNotificacionesActivas())
                .creadoEn(usuario.getCreadoEn())
                .build();
    }

    @Transactional
    public void actualizarPerfil(Long usuarioId, ActualizarPerfilRequest request) {
        usuarioRepository.spActualizarPerfil(
                usuarioId,
                request.getNombre(),
                request.getTelefono());
    }
}
