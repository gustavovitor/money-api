package com.gustavomiranda.money.repository.projection.lancamentos;

import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.domain.enums.TipoLancamento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LancamentoEstatisticaPessoa {

    private TipoLancamento tipo;
    private Pessoa pessoa;
    private BigDecimal total;

    public LancamentoEstatisticaPessoa(TipoLancamento tipo, Pessoa pessoa, BigDecimal total) {
        this.tipo = tipo;
        this.pessoa = pessoa;
        this.total = total;
    }
}
