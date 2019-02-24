package com.gustavomiranda.money.service;

import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    public Pessoa findById(Long id){
        Pessoa p = pessoaRepository.findById(id).orElse(null);
        if(p == null){
            throw new EntityNotFoundException();
        }
        return p;
    }

    public Pessoa updatePessoa(Long id, Pessoa object){
        Pessoa objectSaved = findById(id);
        BeanUtils.copyProperties(object, objectSaved, "id");
        return pessoaRepository.save(objectSaved);
    }

    public Pessoa updateAtivo(Long id, Boolean ativo){
        Pessoa objectSaved = findById(id);
        objectSaved.setAtivo(ativo);
        return pessoaRepository.save(objectSaved);
    }

    public Pessoa validarPessoa(Long id){
        Pessoa p = findById(id);
        if(p.isInativo()){
            return null;
        }
        return p;
    }

}
