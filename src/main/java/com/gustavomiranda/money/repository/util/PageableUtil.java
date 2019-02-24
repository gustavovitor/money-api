package com.gustavomiranda.money.repository.util;

import org.springframework.data.domain.Pageable;

import javax.persistence.TypedQuery;

public class PageableUtil {

    public static void addPageableIntoQuery(TypedQuery<?> query, Pageable pageable) {
        int paginaAtual = pageable.getPageNumber();
        int totalRegistrosPage = pageable.getPageSize();
        int primeiroRegistro = paginaAtual * totalRegistrosPage;

        query.setFirstResult(primeiroRegistro);
        query.setMaxResults(totalRegistrosPage);
    }

}
