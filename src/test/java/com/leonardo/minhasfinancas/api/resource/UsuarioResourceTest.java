package com.leonardo.minhasfinancas.api.resource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leonardo.minhasfinancas.api.dto.UsuarioDto;
import com.leonardo.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.leonardo.minhasfinancas.model.Usuario;
import com.leonardo.minhasfinancas.services.UsuarioService;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration()
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	static final String URI = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private UsuarioService usuarioService;

	private final static String EMAIL = "leonardo@leonardo.com";
	private final static String SENHA = "palmeirasnaotemmundial";

	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception {
		var usuarioDto = UsuarioDto.builder().email(EMAIL).senha(SENHA).build();

		Mockito.when(usuarioService.autenticaUsuario(EMAIL, SENHA)).thenThrow(ErroAutenticacaoException.class);

		String json = new ObjectMapper().writeValueAsString(usuarioDto);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(URI.concat("/autenticar")).accept(JSON)
				.content(json).contentType(JSON);

		this.mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void deveAutenticarUsuario() throws Exception {
		var usuarioDto = UsuarioDto.builder().email(EMAIL).senha(SENHA).build();
		var usuario = Usuario.builder().email(EMAIL).senha(SENHA).id(43829L).build();

		Mockito.when(usuarioService.autenticaUsuario(EMAIL, SENHA)).thenReturn(usuario);

		final String json = new ObjectMapper().writeValueAsString(usuarioDto);

		var request = MockMvcRequestBuilders
				.post(URI.concat("/autenticar"))
				.accept(JSON)
				.contentType(JSON)
				.content(json);

		mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}

	@Test
	public void deveCriarUmNovoUsuario() throws Exception {
		var usuarioDto = UsuarioDto.builder().email(EMAIL).senha(SENHA).build();
		var usuario = Usuario.builder().email(EMAIL).senha(SENHA).id(43829L).build();

		Mockito.when(usuarioService.salvaUsuario(Mockito.any())).thenReturn(usuario);

		final String json = new ObjectMapper().writeValueAsString(usuarioDto);

		var result = mockMvc.perform(MockMvcRequestBuilders.post(URI).content(json).contentType(JSON).accept(JSON))
				.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andReturn();

		String resultDOW = result.getResponse().getContentAsString();
		assertNotNull(resultDOW);
	}

}
