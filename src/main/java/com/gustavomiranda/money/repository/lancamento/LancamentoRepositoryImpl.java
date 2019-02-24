package com.gustavomiranda.money.repository.lancamento;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.repository.filter.LancamentoFilter;
import com.gustavomiranda.money.repository.projection.ResumoLancamento;
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

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;


    /*
    * Método que implementa um filtro dinâmico usando o framework para que na URL das pesquisas eu possa passar variáveis
    * mapeadas na classe LancamentoFilter.
    *
    * Exemplo: http://localhost:8080/lancamentos?dtVencimentoDe=2017-10-01&dtVencimentoAte=2017-10-10
    * */
    @Override
    public Page<Lancamento> filter(LancamentoFilter lancamentoFilter, Pageable pageable){

        /* Cria o builder a partir do manager builder da entidade, pós, cria os critérios de query a partir da classe implementada. */
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        /* Regras da query, "where". */
        Predicate[] predicates = createRestricoes(lancamentoFilter, builder, root);
        criteria.where(predicates);

        /* Executa a query final e retorna a lista de resultados. */
        TypedQuery<Lancamento> query = manager.createQuery(criteria);

        PageableUtil.addPageableIntoQuery(query, pageable);
        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    @Override
    public Page<ResumoLancamento> resume(LancamentoFilter lancamentoFilter, Pageable pageable){
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(builder.construct(ResumoLancamento.class,
                root.get("id"), root.get("descricao"),
                root.get("dataVencimento"), root.get("dataPagamento"),
                root.get("valor"), root.get("tipo"),
                root.get("categoria").get("nome"),
                root.get("pessoa").get("nome")));

        Predicate[] predicates = createRestricoes(lancamentoFilter, builder, root);
        criteria.where(predicates);

        /* Executa a query final e retorna a lista de resultados. */
        TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
        PageableUtil.addPageableIntoQuery(query, pageable);

        return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
    }

    private Long total(LancamentoFilter lancamentoFilter) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        Predicate[] predicates = createRestricoes(lancamentoFilter, builder, root);
        criteria.where(predicates);

        criteria.select(builder.count(root));
        return manager.createQuery(criteria).getSingleResult();
    }

    /*
    * Este método cria uma lista de "predicates" baseado no que fora informado no filtro.
    * */
    private Predicate[] createRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder, Root<Lancamento> root) {
        List<Predicate> predicates = new ArrayList<>();

        /* Monta o where, "where descricao like 'any'"'
        *
        * Usando o metamodel é possível fazer toda a implementação manual de forma automática, sem precisar
        * explanar o builder.like/lower dando get na descrição e nos valores.*/
        if(!StringUtils.isEmpty(lancamentoFilter.getDescricao())){
            predicates.add(
                    builder.like(builder.lower(root.get("descricao")), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%")
            );
        }
        if(lancamentoFilter.getDataVencimentoDe() != null){
            predicates.add(
                    builder.greaterThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoDe())
            );
        }
        if(lancamentoFilter.getDataVencimentoAte() != null){
            predicates.add(
                    builder.lessThanOrEqualTo(root.get("dataVencimento"), lancamentoFilter.getDataVencimentoAte())
            );
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

}
