package com.leonardo.minhasfinancas.repository;

import com.leonardo.minhasfinancas.model.Lancamento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
