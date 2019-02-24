package com.gustavomiranda.money.service;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.service.exceptions.InactiveEntityException;
import com.gustavomiranda.money.repository.LancamentoRepository;
import com.gustavomiranda.money.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class LancamentoService {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    public Lancamento findById(Long id){
        Lancamento l = lancamentoRepository.findById(id).orElse(null);
        if(l == null){
            throw new EntityNotFoundException();
        }
        return l;
    }

    public Lancamento updateLancamento(Long id, Lancamento object){
        Lancamento objectSaved = findById(id);
        if(pessoaService.validarPessoa(object.getPessoa().getId()) == null){
            throw new InactiveEntityException();
        }
        BeanUtils.copyProperties(object, objectSaved, "id");
        return lancamentoRepository.save(objectSaved);
    }

    public Lancamento save(Lancamento lancamento) {
        Pessoa pessoa = pessoaRepository.findById(lancamento.getPessoa().getId()).orElse(null);
        if(pessoa == null || pessoa.isInativo()){
            throw new InactiveEntityException();
        }

        lancamento = lancamentoRepository.save(lancamento);
        Optional<Lancamento> optional = lancamentoRepository.findById(lancamento.getId());
        lancamento = (optional.orElse(null) == null) ? null : optional.get();
        return lancamento;
    }

}
