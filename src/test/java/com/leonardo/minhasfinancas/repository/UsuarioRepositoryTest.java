package com.leonardo.minhasfinancas.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leonardo.minhasfinancas.model.Usuario;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository usuarioRepository;

	private static final String EMAIL_GERAL = "leonardo@email.com";

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		usuarioRepository.save(criarUsuario());

		boolean result = usuarioRepository.existsByEmail(EMAIL_GERAL);

		Assertions.assertThat(result).isTrue();
	}

	@Test
	public void deveRetornarFalseQuandoNaoHouverUsuarioCadastradoComOEmail() {
		Assertions.assertThat(usuarioRepository.existsByEmail("juliana@spfc.com.br")).isFalse();
	}

	@Test
	public void deveRetornarVazioAoBuscarUsuarioComEmailInexistente() {
		Assertions.assertThat(usuarioRepository.findByEmail("naoexiste@email.com").isPresent()).isFalse();
	}

	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		Usuario usuarioSalvo = usuarioRepository.save(criarUsuario());

		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}

	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		usuarioRepository.save(criarUsuario());

		var usuario = usuarioRepository.findByEmail(EMAIL_GERAL);

		Assertions.assertThat(usuario.isPresent()).isTrue();
	}

	private Usuario criarUsuario() {
		return Usuario.builder().nome("Leonardo Nascimento Cintra").email(EMAIL_GERAL).build();
	}
}
