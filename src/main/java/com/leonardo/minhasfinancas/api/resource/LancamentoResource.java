package com.leonardo.minhasfinancas.api.resource;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leonardo.minhasfinancas.api.dto.AtualizaStatusDto;
import com.leonardo.minhasfinancas.api.dto.LancamentoDto;
import com.leonardo.minhasfinancas.enums.StatusLancamento;
import com.leonardo.minhasfinancas.enums.TipoLancamento;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Lancamento;
import com.leonardo.minhasfinancas.services.LancamentoService;
import com.leonardo.minhasfinancas.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

	private final LancamentoService lancamentoService;
	private final UsuarioService usuarioService;

	@PostMapping
	public ResponseEntity salvar(@RequestBody LancamentoDto lancamentoDto) {
		try {
			Lancamento entidade = converter(lancamentoDto);
			lancamentoService.salvar(entidade);

			return new ResponseEntity<Lancamento>(entidade, HttpStatus.CREATED);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDto lancamentoDto) {
		return lancamentoService.buscarPorId(id).map(l -> {
			try {
				Lancamento lancamento = converter(lancamentoDto);
				lancamento.setId(l.getId());
				lancamento.setDataCadastro(ZonedDateTime.now());
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseThrow(() -> new RegraNegocioException("Lançamento não encontrado na base de dados;"));
	}

	@PutMapping("/{id}/atualizar-status")
	public ResponseEntity atualizarStatus(@PathVariable("id") Long id, @RequestBody AtualizaStatusDto dto) {
		return lancamentoService.buscarPorId(id).map(lancamento -> {
			StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());

			if (statusSelecionado == null) {
				return ResponseEntity.badRequest()
						.body("Não foi possivel atualizar o status do lançamento. Envie o status valido");
			}

			try {
				lancamento.setStatus(statusSelecionado);
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseGet(() -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return lancamentoService.buscarPorId(id).map(lancamento -> {
			lancamentoService.deletar(lancamento);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseGet(() -> new ResponseEntity("Lancamento não encontrado na base de dados", HttpStatus.BAD_REQUEST));
	}

	@GetMapping("/{id}")
	public ResponseEntity obterLancamento(@PathVariable("id") Long id) {
		return lancamentoService.buscarPorId(id)
				.map(lancamento -> new ResponseEntity(converter(lancamento), HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity(HttpStatus.NOT_FOUND));
	}

	@GetMapping
	public ResponseEntity buscar(@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano, @RequestParam("usuario") Long idUsuario) {

		Lancamento lancamentoFiltro = new Lancamento();
		lancamentoFiltro.setDescricao(descricao);
		lancamentoFiltro.setMes(mes);
		lancamentoFiltro.setAno(ano);

		var usuario = usuarioService.obterPorId(idUsuario);
		if (usuario.isEmpty()) {
			return ResponseEntity.badRequest().body("Nao foi possivel realizar a consulta. Usuario não encontrado.");
		} else {
			lancamentoFiltro.setUsuario(usuario.get());
		}

		return ResponseEntity.ok(lancamentoService.buscar(lancamentoFiltro));
	}

	private LancamentoDto converter(Lancamento lancamento) {
		return LancamentoDto.builder().id(lancamento.getId()).descricao(lancamento.getDescricao())
				.valor(lancamento.getValor()).mes(lancamento.getMes()).ano(lancamento.getAno())
				.status(lancamento.getStatus().name()).tipo(lancamento.getTipo().name())
				.usuario(lancamento.getUsuario().getId()).build();
	}

	private Lancamento converter(LancamentoDto dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		if (Optional.ofNullable(dto.getTipo()).isPresent()) {
			lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		}
		if (Optional.ofNullable(dto.getStatus()).isPresent()) {
			lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		}
		lancamento.setUsuario(usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Não foi encontrado o usuario (id) informado.")));

		return lancamento;

	}

}
