package com.leonardo.minhasfinancas.services.impl;

import com.leonardo.minhasfinancas.model.Usuario;
import com.leonardo.minhasfinancas.repository.UsuarioRepository;
import com.leonardo.minhasfinancas.services.UsuarioService;

public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;
    
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticaUsuario(String email, String senha) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Usuario salvaUsuario(Usuario usuario) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void validarEmail(String email) {
        // TODO Auto-generated method stub

    }

}
