package com.cavoshcafe.api.repository;

import com.cavoshcafe.api.model.Rol;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Short> {
    Optional<Rol> findByNombre(String nombre);
}
