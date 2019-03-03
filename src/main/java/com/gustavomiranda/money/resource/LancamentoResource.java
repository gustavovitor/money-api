package com.gustavomiranda.money.resource;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.events.CreatedResourceEvent;
import com.gustavomiranda.money.handlers.MoneyExceptionHandler;
import com.gustavomiranda.money.repository.filter.LancamentoFilter;
import com.gustavomiranda.money.repository.projection.ResumoLancamento;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaCategoria;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaDia;
import com.gustavomiranda.money.service.exceptions.InactiveEntityException;
import com.gustavomiranda.money.repository.LancamentoRepository;
import com.gustavomiranda.money.service.LancamentoService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.time.LocalDate;
import java.util.*;

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

    @GetMapping("/relatorios/pessoa")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public ResponseEntity<byte[]> relatorioPorPessoa(@RequestParam(value = "dtInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dtInicio,
                                                     @RequestParam(value = "dtFim") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dtFim) throws Exception{
        byte[] relatorio = lancamentoService.relatorioPorPessoa(dtInicio, dtFim);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE).body(relatorio);
    }

    @GetMapping("/estatistica/categoria")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public List<LancamentoEstatisticaCategoria> porCategoria(@RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return this.lancamentoRepository.porCategoria(date != null ? date : LocalDate.now());
    }

    @GetMapping("/estatistica/dia")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
    public List<LancamentoEstatisticaDia> porDia(@RequestParam(value = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        return this.lancamentoRepository.porDia(date != null ? date : LocalDate.now());
    }

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
    public ResponseEntity<List<Lancamento>> insert(@Valid @RequestBody Lancamento lancamento,
                                                   @RequestParam("qtdMesesReplica") Integer qtdMesesReplica){
        List<Lancamento> lancamentos = new ArrayList<>();
        for(int i = 0; i < qtdMesesReplica; i++){
            Lancamento novoLancamento = new Lancamento();
            BeanUtils.copyProperties(lancamento, novoLancamento);
            lancamentos.add(novoLancamento);
            lancamento.setDataVencimento(lancamento.getDataVencimento().plusMonths(1));
            }
        lancamentos = lancamentoService.saveReplica(lancamentos);
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentos);
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
