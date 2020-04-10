package com.leonardo.minhasfinancas.services;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import com.leonardo.minhasfinancas.model.Usuario;
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

	@SuppressWarnings("unchecked")
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

	@Test
	public void deveRetorarVazioUmLancamentoPorIdQueNaoExiste() {
		final var id = 198323L;

		var lancamento = criarLancamentoDespesaPendente();
		lancamento.setId(id);

		Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.empty());

		var resultado = lancamentoService.buscarPorId(id);

		Assertions.assertThat(resultado.isPresent()).isFalse();

	}

	@Test
	public void deveObterUmLancamentoPorId() {
		final var id = 198323L;

		var lancamento = criarLancamentoDespesaPendente();
		lancamento.setId(id);

		Mockito.when(lancamentoRepository.findById(id)).thenReturn(Optional.of(lancamento));

		var resultado = lancamentoService.buscarPorId(id);

		Assertions.assertThat(resultado.isPresent()).isTrue();

	}

	@Test
	public void deveAtualizarStatusDeUmLancamento() {
		var lancamento = criarLancamentoDespesaPendente();
		lancamento.setId(1L);

		Mockito.doReturn(lancamento).when(lancamentoService).atualizar(lancamento);

		lancamentoService.atualizarStatus(lancamento, StatusLancamento.EFETIVADO);

		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.EFETIVADO);
		Mockito.verify(lancamentoService).atualizar(lancamento);
	}

	@Test
	public void deveLancarErroAoValidarUmLancamento() {
		var lancamento = new Lancamento();

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição valida.");
		lancamento.setDescricao("");

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma descrição valida.");
		lancamento.setDescricao("Salário");

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		lancamento.setMes(100);

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		lancamento.setMes(-390);

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um mês válido.");
		lancamento.setMes(3);

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		lancamento.setAno(300);

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um ano válido.");
		lancamento.setAno(2020);

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário");
		lancamento.setUsuario(
				Usuario.builder().nome("Leonardo Semid").email("leonardo.ncintra@outlook.com").build());

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um usuário");
		lancamento.setUsuario(
				Usuario.builder().id(19823L).nome("Leonardo").email("leonardo.ncintra@outlook.com").build());

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor maior ou igual a 1");
		lancamento.setValor(BigDecimal.valueOf(-100));

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um valor maior ou igual a 1");
		lancamento.setValor(BigDecimal.valueOf(100));

		Assertions.assertThat(Assertions.catchThrowable(() -> lancamentoService.validar(lancamento)))
				.isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
	}

	private Lancamento criarLancamentoDespesaPendente() {
		return Lancamento.builder().ano(2020).mes(2).descricao("lancamento qualquer").tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE).dataCadastro(ZonedDateTime.now()).build();
	}

}
