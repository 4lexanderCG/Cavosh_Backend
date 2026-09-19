package com.cavoshcafe.api.service;

import com.cavoshcafe.api.dto.DireccionRequest;
import com.cavoshcafe.api.model.Direccion;
import com.cavoshcafe.api.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DireccionService {

    @Autowired
    private DireccionRepository direccionRepository;

    public List<Direccion> obtenerDireccionesUsuario(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public void agregarDireccion(Long usuarioId, DireccionRequest request) {
        direccionRepository.spAgregarDireccion(
                usuarioId,
                request.getAlias(),
                request.getDireccion(),
                request.getReferencia(),
                request.getEsPrincipal() != null ? request.getEsPrincipal() : false);
    }

    @Transactional
    public void eliminarDireccion(Long direccionId) {
        direccionRepository.deleteById(direccionId);
    }
}
