package com.gustavomiranda.money.repository.pessoa;

import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.repository.filter.PessoaFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PessoaRepositoryQuery {
    public Page<Pessoa> filter(PessoaFilter pessoaFilter, Pageable pageable);
}
