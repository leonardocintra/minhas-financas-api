package com.leonardo.minhasfinancas.api.resource;

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

import com.leonardo.minhasfinancas.api.dto.LancamentoDto;
import com.leonardo.minhasfinancas.enums.StatusLancamento;
import com.leonardo.minhasfinancas.enums.TipoLancamento;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Lancamento;
import com.leonardo.minhasfinancas.services.LancamentoService;
import com.leonardo.minhasfinancas.services.UsuarioService;

@RestController
@RequestMapping("/api/lancamentos")
public class LancamentoResource {

	private LancamentoService lancamentoService;
	private UsuarioService usuarioService;

	private LancamentoResource(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

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
				lancamentoService.atualizar(lancamento);
				return ResponseEntity.ok(lancamento);
			} catch (RegraNegocioException e) {
				return ResponseEntity.badRequest().body(e.getMessage());
			}
		}).orElseThrow(() -> new RegraNegocioException("Lançamento não encontrado na base de dados;"));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
		return lancamentoService.buscarPorId(id).map(lancamento -> {
			lancamentoService.deletar(lancamento);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}).orElseThrow(() -> new RegraNegocioException("Lançamento não encontrado na base de dados;"));
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

	private Lancamento converter(LancamentoDto dto) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(dto.getId());
		lancamento.setDescricao(dto.getDescricao());
		lancamento.setAno(dto.getAno());
		lancamento.setMes(dto.getMes());
		lancamento.setValor(dto.getValor());
		lancamento.setTipo(TipoLancamento.valueOf(dto.getTipo()));
		lancamento.setStatus(StatusLancamento.valueOf(dto.getStatus()));
		lancamento.setUsuario(usuarioService.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Não foi encontrado o usuario (id) informado.")));

		return lancamento;

	}

}
