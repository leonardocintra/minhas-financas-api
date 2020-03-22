package com.leonardo.minhasfinancas.services;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leonardo.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Usuario;
import com.leonardo.minhasfinancas.repository.UsuarioRepository;
import com.leonardo.minhasfinancas.services.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class UsuarioServiceTest {

	@SpyBean
	UsuarioServiceImpl usuarioService;

	@MockBean
	UsuarioRepository usuarioRepository;

	@Test
	public void deveSalvarUmUsuario() {
		Mockito.doNothing().when(usuarioService).validarEmail(Mockito.anyString());
		Usuario usuario = criarUsuarioComId();
		Mockito.when(usuarioRepository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

		var usuarioSalvo = usuarioService.salvaUsuario(new Usuario());

		Assertions.assertNotNull(usuarioSalvo);
		Assertions.assertTrue(usuarioSalvo.getId().equals(1L));
		Assertions.assertTrue(usuarioSalvo.getNome().equals("Leonardo"));
		Assertions.assertTrue(usuarioSalvo.getEmail().equals("email@email.com"));
		Assertions.assertTrue(usuarioSalvo.getSenha().equals("123456"));
	}

	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		var usuario = criarUsuarioComId();
		Mockito.doThrow(RegraNegocioException.class).when(usuarioService).validarEmail("email@email.com");
				
		Assertions.assertThrows(RegraNegocioException.class, () -> usuarioService.salvaUsuario(usuario));
		Mockito.verify(usuarioRepository, Mockito.never()).save(usuario);	
		
	}

	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		final String email = "email@email.com.br";
		final String senha = "123456";

		var usuario = Usuario.builder().nome("Teste").email(email).senha(senha).build();
		Mockito.when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

		var result = usuarioService.autenticaUsuario(email, senha);

		Assertions.assertNotNull(result);
	}

	@Test
	public void deveLancarErroQuandNaoEncontrarUsuarioPeloEmailInformado() {
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

		ErroAutenticacaoException exception = assertThrows(ErroAutenticacaoException.class,
				() -> usuarioService.autenticaUsuario("email@qualquer.com.br", "123456"));

		Assertions.assertTrue(exception.getMessage().equals("Usuário não encontrado pelo email informado"));
	}

	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		final String email = "email@qualquer.com";
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString()))
				.thenReturn(Optional.of(Usuario.builder().nome("Juliana").email(email).senha("julianasenha").build()));

		ErroAutenticacaoException exception = assertThrows(ErroAutenticacaoException.class,
				() -> usuarioService.autenticaUsuario(email, "123456"));

		Assertions.assertTrue(exception.getMessage().equals("Senha invalida"));
	}

	@Test
	public void deveValidarEmail() {
		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(false);

		Assertions.assertDoesNotThrow(() -> usuarioService.validarEmail("email@mail.com"));
	}

	@Test
	public void deveLancarErroQuandoExistirEmailCadastrado() {
		Mockito.when(usuarioRepository.existsByEmail(Mockito.anyString())).thenReturn(true);

		Assertions.assertThrows(RegraNegocioException.class, () -> usuarioService.validarEmail(Mockito.anyString()));
	}

	private Usuario criarUsuarioComId() {
		return Usuario.builder().id(1L).nome("Leonardo").email("email@email.com").senha("123456").build();
	}
}
