package com.gustavomiranda.money.repository.lancamento;

import com.gustavomiranda.money.domain.Categoria_;
import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.domain.Lancamento_;
import com.gustavomiranda.money.domain.Pessoa_;
import com.gustavomiranda.money.repository.filter.LancamentoFilter;
import com.gustavomiranda.money.repository.projection.ResumoLancamento;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaCategoria;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaDia;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaPessoa;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

    @PersistenceContext
    private EntityManager manager;


    @Override
    public List<LancamentoEstatisticaPessoa> porPessoa(LocalDate dtInicio, LocalDate dtFim) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaPessoa> criteria = builder.createQuery(LancamentoEstatisticaPessoa.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(builder.construct(LancamentoEstatisticaPessoa.class,
                root.get(Lancamento_.tipo), root.get(Lancamento_.pessoa), builder.sum(root.get(Lancamento_.valor))));

        criteria.where(
                builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), dtInicio),
                builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), dtFim)
        );

        criteria.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.pessoa), root.get(Lancamento_.categoria));

        TypedQuery<LancamentoEstatisticaPessoa> typedQuery = manager.createQuery(criteria);

        return typedQuery.getResultList();
    }

    @Override
    public List<LancamentoEstatisticaDia> porDia(LocalDate dia) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaDia> criteria = builder.createQuery(LancamentoEstatisticaDia.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(builder.construct(LancamentoEstatisticaDia.class,
                root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento), builder.sum(root.get(Lancamento_.valor))));

        criteria.where(
                builder.equal(root.get(Lancamento_.dataVencimento), dia)
        );

        criteria.groupBy(root.get(Lancamento_.tipo), root.get(Lancamento_.dataVencimento));

        TypedQuery<LancamentoEstatisticaDia> typedQuery = manager.createQuery(criteria);

        return typedQuery.getResultList();
    }

    @Override
    public List<LancamentoEstatisticaCategoria> porCategoria(LocalDate mes) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<LancamentoEstatisticaCategoria> criteria = builder.createQuery(LancamentoEstatisticaCategoria.class);
        Root<Lancamento> root = criteria.from(Lancamento.class);

        criteria.select(builder.construct(LancamentoEstatisticaCategoria.class,
                root.get(Lancamento_.categoria), builder.sum(root.get(Lancamento_.valor))));

        LocalDate primeiroDia = mes.withDayOfMonth(1);
        LocalDate ultimoDia = mes.withDayOfMonth(mes.lengthOfMonth());

        criteria.where(
                builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), primeiroDia),
                builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), ultimoDia)
        );

        criteria.groupBy(root.get(Lancamento_.categoria));

        TypedQuery<LancamentoEstatisticaCategoria> typedQuery = manager.createQuery(criteria);

        return typedQuery.getResultList();
    }

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
                root.get(Lancamento_.id), root.get(Lancamento_.descricao),
                root.get(Lancamento_.dataVencimento), root.get(Lancamento_.dataPagamento),
                root.get(Lancamento_.valor), root.get(Lancamento_.tipo),
                root.get(Lancamento_.categoria).get(Categoria_.nome),
                root.get(Lancamento_.pessoa).get(Pessoa_.nome)));

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
                    builder.like(builder.lower(root.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%")
            );
        }
        if(lancamentoFilter.getDataVencimentoDe() != null){
            predicates.add(
                    builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe())
            );
        }
        if(lancamentoFilter.getDataVencimentoAte() != null){
            predicates.add(
                    builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte())
            );
        }

        return predicates.toArray(new Predicate[predicates.size()]);
    }

}
