package com.leonardo.minhasfinancas.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.repository.UsuarioRepository;
import com.leonardo.minhasfinancas.services.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class UsuarioServiceTest {

	UsuarioService usuarioService;

	@MockBean
	UsuarioRepository usuarioRepository;

	@BeforeAll
	public void initAll() {
		usuarioService = new UsuarioServiceImpl(usuarioRepository);
	}

	@AfterAll
	public void finalizeAll() {
		System.out.println("UsuarioServiceTest finalizados!");
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
}
