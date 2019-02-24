package com.gustavomiranda.money.resource;

import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.events.CreatedResourceEvent;
import com.gustavomiranda.money.repository.PessoaRepository;
import com.gustavomiranda.money.repository.filter.PessoaFilter;
import com.gustavomiranda.money.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
    public Page<Pessoa> findAll(PessoaFilter pessoaFilter, Pageable pageable){
        return this.pessoaRepository.filter(pessoaFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
    public ResponseEntity<Pessoa> insert(@Valid @RequestBody Pessoa pessoa, HttpServletResponse response){
        pessoa = pessoaRepository.save(pessoa);
        publisher.publishEvent(new CreatedResourceEvent(this, response, pessoa.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(pessoa);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Pessoa> optional = pessoaRepository.findById(id);
        return (optional.orElse(null) == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(optional.get());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id){
        pessoaRepository.delete(pessoaRepository.getOne(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
    public ResponseEntity<Pessoa> updatePessoa(@PathVariable Long id, @Valid @RequestBody Pessoa object){
        return ResponseEntity.ok(pessoaService.updatePessoa(id, object));
    }

    @PutMapping("/{id}/ativo")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
    public ResponseEntity<Pessoa> updateAtivo(@PathVariable Long id, @RequestBody Boolean ativo){
        return ResponseEntity.ok(pessoaService.updateAtivo(id, ativo));
    }

}
