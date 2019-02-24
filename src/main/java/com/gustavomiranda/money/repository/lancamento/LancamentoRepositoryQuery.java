package com.gustavomiranda.money.repository.lancamento;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.repository.filter.LancamentoFilter;
import com.gustavomiranda.money.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LancamentoRepositoryQuery {
    public Page<Lancamento> filter(LancamentoFilter lancamentoFilter, Pageable pageable);
    public Page<ResumoLancamento> resume(LancamentoFilter lancamentoFilter, Pageable pageable);
}
