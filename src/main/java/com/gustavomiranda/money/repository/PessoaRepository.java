package com.gustavomiranda.money.repository;

import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.repository.pessoa.PessoaRepositoryQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>, PessoaRepositoryQuery {
}
