package com.leonardo.minhasfinancas.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leonardo.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Usuario;
import com.leonardo.minhasfinancas.repository.UsuarioRepository;
import com.leonardo.minhasfinancas.services.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository usuarioRepository;
        public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        super();
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public Usuario autenticaUsuario(final String email, final String senha) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isEmpty()) {
        	throw new ErroAutenticacaoException("Usuário não encontrado pelo email informado");
        }
        
        if (usuario.map(u -> u.getSenha()).map(s -> s.equals(email)).orElse(false)) {
        	throw new ErroAutenticacaoException("Senha invalida");        	
        }
        
        return usuario.get();
    }

    @Override
    @Transactional
    public Usuario salvaUsuario(final Usuario usuario) {
        validarEmail(usuario.getEmail());        
        return usuarioRepository.save(usuario);
    }

    @Override
    public void validarEmail(final String email) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraNegocioException("Ja existe um usuário com este email.");
        }
    }

}
