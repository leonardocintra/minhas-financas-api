package com.leonardo.minhasfinancas.repository;

import com.leonardo.minhasfinancas.model.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}
