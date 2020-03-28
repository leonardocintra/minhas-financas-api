package com.leonardo.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leonardo.minhasfinancas.api.dto.UsuarioDto;
import com.leonardo.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Usuario;
import com.leonardo.minhasfinancas.services.LancamentoService;
import com.leonardo.minhasfinancas.services.UsuarioService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioResource {

	private final UsuarioService usuarioService;
	private final LancamentoService lancamentoService;

	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDto dto) {
		try {
			var usuario = usuarioService.autenticaUsuario(dto.getEmail(), dto.getSenha());
			return ResponseEntity.ok(usuario);
		} catch (ErroAutenticacaoException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}

	}

	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDto dto) {
		var usuario = Usuario.builder().nome(dto.getNome()).email(dto.getEmail()).senha(dto.getSenha()).build();

		try {
			var usuarioSalvo = usuarioService.salvaUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
		} catch (RegraNegocioException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
	}

	@GetMapping("/{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") final Long id) {
		var usuario = usuarioService.obterPorId(id);

		if (usuario.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(lancamentoService.obterSaldoPorUsuario(id));
	}
}
