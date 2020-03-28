package com.leonardo.minhasfinancas.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leonardo.minhasfinancas.enums.StatusLancamento;
import com.leonardo.minhasfinancas.enums.TipoLancamento;
import com.leonardo.minhasfinancas.exceptions.RegraNegocioException;
import com.leonardo.minhasfinancas.model.Lancamento;
import com.leonardo.minhasfinancas.repository.LancamentoRepository;
import com.leonardo.minhasfinancas.services.LancamentoService;

@Service
public class LancamentoSeriviceImpl implements LancamentoService {

	private LancamentoRepository lancamentoRepository;

	public LancamentoSeriviceImpl(LancamentoRepository lancamentoRepository) {
		this.lancamentoRepository = lancamentoRepository;
	}

	@Override
	@Transactional
	public Lancamento salvar(final Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(final Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		validar(lancamento);
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(final Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(final Lancamento lancamento) {
		Example<Lancamento> example = Example.of(lancamento,
				ExampleMatcher.matching().withIgnoreCase().withStringMatcher(StringMatcher.CONTAINING));
		
		return lancamentoRepository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, final StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);

	}

	@Override
	public void validar(final Lancamento lancamento) {
		if (lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma descrição valida.");
		}
		
		if (lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um mês válido.");
		}
		
		if (lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um ano válido.");
		}
		
		if (lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um usuário");
		}
		
		if (lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1 ) {
			throw new RegraNegocioException("Inform um valor maior ou igual a 1");
		}
		
		if (lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de lançamento.");
		}
	}

	@Override
	public Optional<Lancamento> buscarPorId(final Long id) {
		return lancamentoRepository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(final Long id) {
		BigDecimal receitas = lancamentoRepository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA);
		BigDecimal despesas = lancamentoRepository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA);
		
		if (receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		
		if (despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		
		return receitas.subtract(despesas);
	}

}
