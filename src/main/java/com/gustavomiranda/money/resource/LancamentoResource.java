package com.gustavomiranda.money.resource;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.events.CreatedResourceEvent;
import com.gustavomiranda.money.exceptionshandler.MoneyExceptionHandler;
import com.gustavomiranda.money.repository.filter.LancamentoFilter;
import com.gustavomiranda.money.repository.projection.ResumoLancamento;
import com.gustavomiranda.money.service.exceptions.InactiveEntityException;
import com.gustavomiranda.money.repository.LancamentoRepository;
import com.gustavomiranda.money.service.LancamentoService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource {

    @Autowired
    private LancamentoService lancamentoService;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private MessageSource messageSource;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public Page<Lancamento> findAll(LancamentoFilter lancamentoFilter, Pageable pageable){
        return this.lancamentoRepository.filter(lancamentoFilter, pageable);
    }

    @GetMapping(params = "resumo")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public Page<ResumoLancamento> resume(LancamentoFilter lancamentoFilter, Pageable pageable){
        return this.lancamentoRepository.resume(lancamentoFilter, pageable);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
    public ResponseEntity<Lancamento> insert(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response){
        Lancamento l = lancamentoService.save(lancamento);
        publisher.publishEvent(new CreatedResourceEvent(this, response, lancamento.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(l);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id){
        lancamentoRepository.delete(lancamentoRepository.getOne(id));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Lancamento> optional = lancamentoRepository.findById(id);
        return (optional.orElse(null) == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(optional.get());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
    public ResponseEntity<Lancamento> updateLancamento(@PathVariable Long id, @Valid @RequestBody Lancamento object){
        return ResponseEntity.ok(lancamentoService.updateLancamento(id, object));
    }

    @ExceptionHandler({ InactiveEntityException.class })
    public ResponseEntity<Object> inactiveEntityException(InactiveEntityException ex) {
        String mensagem = messageSource.getMessage("pessoa.inexistente-inativa", null, LocaleContextHolder.getLocale());
        List<MoneyExceptionHandler.ErrorHandler> erros = Arrays.asList(new MoneyExceptionHandler.ErrorHandler(mensagem, ExceptionUtils.getRootCauseMessage(ex)));
        return ResponseEntity.badRequest().body(erros);
    }
}
