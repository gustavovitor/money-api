package com.gustavomiranda.money.repository.filter;

import lombok.ToString;

@ToString
public class PessoaFilter {

    private String nome;

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
