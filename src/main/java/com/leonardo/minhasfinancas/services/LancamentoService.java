package com.leonardo.minhasfinancas.services;

import java.util.List;
import java.util.Optional;

import com.leonardo.minhasfinancas.enums.StatusLancamento;
import com.leonardo.minhasfinancas.model.Lancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);

	Lancamento atualizar(Lancamento lancamento);

	void deletar(Lancamento lancamento);

	List<Lancamento> buscar(Lancamento lancamento);

	void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
	Optional<Lancamento> buscarPorId(Long id);
}
