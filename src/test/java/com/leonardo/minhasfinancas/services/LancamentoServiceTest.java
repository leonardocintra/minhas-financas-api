package com.leonardo.minhasfinancas.services;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leonardo.minhasfinancas.enums.StatusLancamento;
import com.leonardo.minhasfinancas.enums.TipoLancamento;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Lancamento;
import com.leonardo.minhasfinancas.repository.LancamentoRepository;
import com.leonardo.minhasfinancas.services.impl.LancamentoSeriviceImpl;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoSeriviceImpl lancamentoService;

	@MockBean
	LancamentoRepository lancamentoRepository;

	@Test
	public void deveSalvarUmLancamento() {
		var lancamentoASalvar = criarLancamentoDespesaPendente();
		Mockito.doNothing().when(lancamentoService).validar(lancamentoASalvar);

		var lancamentoSalvo = criarLancamentoDespesaPendente();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.EFETIVADO);
		Mockito.when(lancamentoRepository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);

		var lancamento = lancamentoService.salvar(lancamentoASalvar);

		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.EFETIVADO);
	}

	@Test
	public void naoDeveSalvarLancamentoQuandoTiverUmErroDeValidacao() {
		var lancamentoASalvar = criarLancamentoDespesaPendente();
		Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoASalvar);

		Assertions.catchThrowableOfType(() -> lancamentoService.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveAtualizarUmLancamento() {
		var lancamentoSalvo = criarLancamentoDespesaPendente();
		lancamentoSalvo.setId(1L);

		Mockito.doNothing().when(lancamentoService).validar(lancamentoSalvo);
		Mockito.when(lancamentoRepository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

		lancamentoService.atualizar(lancamentoSalvo);

		Mockito.verify(lancamentoRepository, Mockito.times(1)).save(lancamentoSalvo);
	}

	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		var lancamentoASalvar = criarLancamentoDespesaPendente();
		Mockito.doThrow(RegraNegocioException.class).when(lancamentoService).validar(lancamentoASalvar);

		Assertions.catchThrowableOfType(() -> lancamentoService.atualizar(lancamentoASalvar),
				NullPointerException.class);
		Mockito.verify(lancamentoRepository, Mockito.never()).save(lancamentoASalvar);
	}

	@Test
	public void deveDeletarUmLancamento() {
		var lancamento = criarLancamentoDespesaPendente();
		lancamento.setId(183L);

		lancamentoService.deletar(lancamento);

		Mockito.verify(lancamentoRepository).delete(lancamento);
	}

	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoInexistente() {
		var lancamento = criarLancamentoDespesaPendente();

		Assertions.catchThrowableOfType(() -> lancamentoService.deletar(lancamento), NullPointerException.class);

		Mockito.verify(lancamentoRepository, Mockito.never()).delete(lancamento);
	}

	@Test
	public void deveFiltrarLancamentos() {
		var lancamento = criarLancamentoDespesaPendente();
		lancamento.setId(3829L);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(lancamentoRepository.findAll(Mockito.any(org.springframework.data.domain.Example.class)))
				.thenReturn(lista);

		var resultado = lancamentoService.buscar(lancamento);

		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	private Lancamento criarLancamentoDespesaPendente() {
		return Lancamento.builder().ano(2020).mes(2).descricao("lancamento qualquer").tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE).dataCadastro(ZonedDateTime.now()).build();
	}

}
