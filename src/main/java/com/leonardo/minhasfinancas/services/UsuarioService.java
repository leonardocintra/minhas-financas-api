package com.leonardo.minhasfinancas.services;

import java.util.Optional;

import com.leonardo.minhasfinancas.model.Usuario;

public interface UsuarioService {

    Usuario autenticaUsuario(String email, String senha);

    Usuario salvaUsuario(Usuario usuario);

    void validarEmail(String email);
    
    Optional<Usuario> obterPorId(Long id);
}
