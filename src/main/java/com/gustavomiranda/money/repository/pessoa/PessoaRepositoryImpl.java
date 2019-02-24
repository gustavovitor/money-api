package com.gustavomiranda.money.repository.pessoa;

import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.repository.filter.PessoaFilter;
import com.gustavomiranda.money.repository.util.PageableUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class PessoaRepositoryImpl implements PessoaRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;

    @Override
    public Page<Pessoa> filter(PessoaFilter pessoaFilter, Pageable pageable) {


        /* Cria o builder a partir do manager builder da entidade, pós, cria os critérios de query a partir da classe implementada. */
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Pessoa> criteria = builder.createQuery(Pessoa.class);
        Root<Pessoa> root = criteria.from(Pessoa.class);

        /* Regras da query, "where". */
        Predicate[] predicates = createRestricoes(pessoaFilter, builder, root);
        criteria.where(predicates);

        /* Executa a query final e retorna a lista de resultados. */
        TypedQuery<Pessoa> query = manager.createQuery(criteria);

        PageableUtil.addPageableIntoQuery(query, pageable);
        return new PageImpl<>(query.getResultList(), pageable, total(pessoaFilter));
    }

    private Predicate[] createRestricoes(PessoaFilter pessoaFilter, CriteriaBuilder builder, Root<Pessoa> root) {
        List<Predicate> predicates = new ArrayList<>();

        /* Monta o where, "where descricao like 'any'"'
         *
         * Usando o metamodel é possível fazer toda a implementação manual de forma automática, sem precisar
         * explanar o builder.like/lower dando get na descrição e nos valores.*/
        if(!StringUtils.isEmpty(pessoaFilter.getNome())){
            predicates.add(
                    builder.like(builder.lower(root.get("nome")), "%" + pessoaFilter.getNome().toLowerCase() + "%")
            );
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

    private Long total(PessoaFilter pessoaFilter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Pessoa> root = criteria.from(Pessoa.class);

        Predicate[] predicates = createRestricoes(pessoaFilter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));
        return manager.createQuery(criteria).getSingleResult();
    }
}
