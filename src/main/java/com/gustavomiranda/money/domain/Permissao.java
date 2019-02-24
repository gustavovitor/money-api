package com.gustavomiranda.money.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "permissao")
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Permissao {

    @Id
    private Long id;
    private String descricao;
}
