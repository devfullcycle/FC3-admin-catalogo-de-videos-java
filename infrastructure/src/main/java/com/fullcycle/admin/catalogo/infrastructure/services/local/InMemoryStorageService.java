package com.fullcycle.admin.catalogo.infrastructure.services.local;

import com.fullcycle.admin.catalogo.domain.video.Resource;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorageService implements StorageService {

    private final Map<String, Resource> storage;

    public InMemoryStorageService() {
        this.storage = new ConcurrentHashMap<>();
    }

    public void clear() {
        this.storage.clear();
    }

    public Map<String, Resource> storage() {
        return this.storage;
    }

    @Override
    public void store(final String id, final Resource resource) {
        this.storage.put(id, resource);
    }

    @Override
    public Optional<Resource> get(final String id) {
        return Optional.ofNullable(this.storage.get(id));
    }

    @Override
    public List<String> list(final String prefix) {
        return this.storage.keySet().stream()
                .filter(it -> it.startsWith(prefix))
                .toList();
    }

    @Override
    public void deleteAll(final List<String> ids) {
        ids.forEach(this.storage::remove);
    }
}
