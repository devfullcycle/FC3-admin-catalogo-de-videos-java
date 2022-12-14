package com.fullcycle.admin.catalogo.infrastructure.services;

import com.fullcycle.admin.catalogo.domain.video.Resource;

import java.util.List;
import java.util.Optional;

public interface StorageService {

    void store(String id, Resource resource);

    Optional<Resource> get(String id);

    List<String> list(String prefix);

    void deleteAll(final List<String> ids);
}
