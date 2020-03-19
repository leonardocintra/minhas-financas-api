package com.leonardo.minhasfinancas.services;

import com.leonardo.minhasfinancas.model.Usuario;

public interface UsuarioService {

    Usuario autenticaUsuario(String email, String senha);

    Usuario salvaUsuario(Usuario usuario);

    void validarEmail(String email);
}
