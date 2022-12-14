package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.domain.video.Resource;

public record MediaOutput(
        byte[] content,
        String contentType,
        String name
) {
    public static MediaOutput with(final Resource aResource) {
        return new MediaOutput(
                aResource.content(),
                aResource.contentType(),
                aResource.name()
        );
    }
}
