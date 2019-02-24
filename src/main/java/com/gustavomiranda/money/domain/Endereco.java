package com.gustavomiranda.money.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;
import javax.validation.constraints.Max;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Embeddable
public class Endereco {

    @Max(value = 120)
    private String logradouro;
    @Max(value = 32)
    private String numero;
    @Max(value = 32)
    private String complemento;
    @Max(value = 60)
    private String bairro;
    @Max(value = 30)
    private String cep;
    @Max(value = 80)
    private String cidade;
    @Max(value = 80)
    private String estado;

}
