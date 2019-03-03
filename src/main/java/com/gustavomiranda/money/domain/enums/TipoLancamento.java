package com.gustavomiranda.money.domain.enums;

import lombok.Getter;

@Getter
public enum TipoLancamento {
    RECEITA("Receita"),
    DESPESA("Despesa");

    private String descricao;

    TipoLancamento(String descricao) {
        this.descricao = descricao;
    }
}
