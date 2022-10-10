package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, UUID> {
}
