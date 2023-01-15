package com.fullcycle.admin.catalogo.infrastructure.services.impl;

import com.fullcycle.admin.catalogo.domain.video.Resource;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class GCStorageService implements StorageService {

    private final String bucket;
    private final Storage storage;

    public GCStorageService(final String bucket, final Storage storage) {
        this.bucket = bucket;
        this.storage = storage;
    }

    @Override
    public void store(final String id, final Resource resource) {
        final var info = BlobInfo.newBuilder(this.bucket, id)
                .setContentType(resource.contentType())
                .setCrc32cFromHexString(resource.checksum())
                .build();

        this.storage.create(info, resource.content());
    }

    @Override
    public Optional<Resource> get(final String id) {
        return Optional.ofNullable(this.storage.get(this.bucket, id))
                .map(blob -> Resource.with(
                        blob.getContent(),
                        blob.getCrc32cToHexString(),
                        blob.getContentType(),
                        blob.getName()
                ));
    }

    @Override
    public List<String> list(final String prefix) {
        final var blobs = this.storage.list(bucket, Storage.BlobListOption.prefix(prefix));

        return StreamSupport.stream(blobs.iterateAll().spliterator(), false)
                .map(BlobInfo::getBlobId)
                .map(BlobId::getName)
                .toList();
    }

    @Override
    public void deleteAll(final List<String> ids) {
        if (ids == null || ids.isEmpty())
            return;

        final var blobs = ids.stream()
                .map(id -> BlobId.of(this.bucket, id))
                .toList();

        this.storage.delete(blobs);
    }
}
