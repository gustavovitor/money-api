package com.gustavomiranda.money.repository;

import com.gustavomiranda.money.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {}
