package com.gustavomiranda.money.repository.lancamento;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.repository.filter.LancamentoFilter;
import com.gustavomiranda.money.repository.projection.ResumoLancamento;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaCategoria;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaDia;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaPessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface LancamentoRepositoryQuery {
    public Page<Lancamento> filter(LancamentoFilter lancamentoFilter, Pageable pageable);
    public Page<ResumoLancamento> resume(LancamentoFilter lancamentoFilter, Pageable pageable);
    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mes);
    public List<LancamentoEstatisticaDia> porDia(LocalDate dia);
    public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate dtInicio, LocalDate dtFim);
}
