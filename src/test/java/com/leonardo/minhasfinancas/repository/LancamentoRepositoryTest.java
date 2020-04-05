package com.leonardo.minhasfinancas.repository;

import java.time.ZonedDateTime;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.leonardo.minhasfinancas.enums.StatusLancamento;
import com.leonardo.minhasfinancas.enums.TipoLancamento;
import com.leonardo.minhasfinancas.model.Lancamento;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

	@Autowired
	LancamentoRepository lancamentoRepository;

	@Autowired
	TestEntityManager entityManager;

	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = criarLancamentoDespesaPendente();

		var result = lancamentoRepository.save(lancamento);

		Assertions.assertThat(result.getId()).isNotNull();
	}

	@Test
	public void deveDeletarLancamento() {
		var lancamento = criarEPersistirLancamento();

		lancamento = entityManager.find(Lancamento.class, lancamento.getId());

		lancamentoRepository.delete(lancamento);

		var lancamentoInexistente = entityManager.find(Lancamento.class, lancamento.getId());

		Assertions.assertThat(lancamentoInexistente).isNull();
	}

	@Test
	public void deveAtualizarUmLancamento() {
		var lancamento = criarEPersistirLancamento();

		lancamento.setAno(2019);
		lancamento.setDescricao("Teste de atualização");
		lancamento.setStatus(StatusLancamento.CANCELADO);
		lancamentoRepository.save(lancamento);

		var lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());

		Assertions.assertThat(lancamentoAtualizado.getAno()).isEqualTo(2019);
		Assertions.assertThat(lancamentoAtualizado.getDescricao()).isEqualTo("Teste de atualização");
		Assertions.assertThat(lancamentoAtualizado.getStatus()).isEqualTo(StatusLancamento.CANCELADO);

	}
	
	@Test
	public void deveBuscarUmLancamento() {
		var lancamento = criarEPersistirLancamento();
		var result = lancamentoRepository.findById(lancamento.getId());
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}

	private Lancamento criarEPersistirLancamento() {
		var lancamento = criarLancamentoDespesaPendente();
		lancamento = entityManager.persist(lancamento);
		return lancamento;
	}

	private Lancamento criarLancamentoDespesaPendente() {
		return Lancamento.builder().ano(2020).mes(2).descricao("lancamento qualquer").tipo(TipoLancamento.RECEITA)
				.status(StatusLancamento.PENDENTE).dataCadastro(ZonedDateTime.now()).build();
	}
}
