package com.gustavomiranda.money.service;

import com.gustavomiranda.money.domain.Lancamento;
import com.gustavomiranda.money.domain.Pessoa;
import com.gustavomiranda.money.repository.projection.lancamentos.LancamentoEstatisticaPessoa;
import com.gustavomiranda.money.service.exceptions.InactiveEntityException;
import com.gustavomiranda.money.repository.LancamentoRepository;
import com.gustavomiranda.money.repository.PessoaRepository;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public byte[] relatorioPorPessoa(LocalDate dtInicio, LocalDate dtFim) throws Exception {
        List<LancamentoEstatisticaPessoa> dados = lancamentoRepository.porPessoa(dtInicio, dtFim);
        Map<String, Object> params = new HashMap<>();
        params.put("DT_INICIO", Date.valueOf(dtInicio));
        params.put("DT_FIM", Date.valueOf(dtFim));

        InputStream inputStream = this.getClass().getResourceAsStream("/rel/lancamento-por-pessoa.jasper");

        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, params, new JRBeanCollectionDataSource(dados));

        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    private Lancamento findById(Long id){
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

        Lancamento lancamentoSalvo = lancamentoRepository.save(lancamento);
        Optional<Lancamento> optional = lancamentoRepository.findById(lancamentoSalvo.getId());
        lancamentoSalvo = (optional.orElse(null) == null) ? null : optional.get();
        return lancamentoSalvo;
    }

    public List<Lancamento> saveReplica(List<Lancamento> lancamentos) {
        lancamentos.forEach(x -> {
            Pessoa p = pessoaRepository.findById(x.getPessoa().getId()).orElse(null);
            if(p == null || p.isInativo()) {
                throw new InactiveEntityException();
            }
        });
        return lancamentoRepository.saveAll(lancamentos);
    }

}
