package com.gustavomiranda.money.repository;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.repository.lancamento.LancamentoRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {}
