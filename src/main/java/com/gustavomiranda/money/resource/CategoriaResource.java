package com.gustavomiranda.money.resource;

import com.gustavomiranda.money.domain.Categoria;
import com.gustavomiranda.money.events.CreatedResourceEvent;
import com.gustavomiranda.money.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ApplicationEventPublisher publisher;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
    public List<Categoria> findAll(){
        return this.categoriaRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('write')")
    public ResponseEntity<Categoria> insert(@Valid @RequestBody Categoria categoria, HttpServletResponse response){
        categoria = categoriaRepository.save(categoria);
        publisher.publishEvent(new CreatedResourceEvent(this, response, categoria.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(categoria);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
    public ResponseEntity<?> findById(@PathVariable Long id){
        Optional<Categoria> optional = categoriaRepository.findById(id);
        return (optional.orElse(null) == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(optional.get());
    }
}
