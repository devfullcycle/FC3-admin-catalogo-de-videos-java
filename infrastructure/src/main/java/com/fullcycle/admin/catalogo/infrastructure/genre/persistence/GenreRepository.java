package com.fullcycle.admin.catalogo.infrastructure.genre.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface GenreRepository extends JpaRepository<GenreJpaEntity, String> {

    Page<GenreJpaEntity> findAll(Specification<GenreJpaEntity> whereClause, Pageable page);

    @Query(value = "select g.id from Genre g where g.id in :ids")
    List<String> existsByIds(@Param("ids") List<String> ids);
}
