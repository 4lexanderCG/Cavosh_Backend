package com.cavoshcafe.api.service;

import com.cavoshcafe.api.dto.AuthResponse;
import com.cavoshcafe.api.dto.LoginRequest;
import com.cavoshcafe.api.dto.RegisterRequest;
import com.cavoshcafe.api.model.Usuario;
import com.cavoshcafe.api.repository.UsuarioRepository;
import com.cavoshcafe.api.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El correo electrónico ya se encuentra registrado");
        }

        String passwordCifrada = passwordEncoder.encode(request.getPassword());

        Long usuarioId = usuarioRepository.spRegistrarUsuario(
                request.getNombre(),
                request.getEmail(),
                request.getTelefono(),
                passwordCifrada);

        String token = tokenProvider.generarToken(request.getEmail());

        return AuthResponse.builder()
                .token(token)
                .usuarioId(usuarioId)
                .nombre(request.getNombre())
                .email(request.getEmail())
                .rol("cliente")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String token = tokenProvider.generarToken(usuario.getEmail());

        return AuthResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().getNombre())
                .build();
    }
}