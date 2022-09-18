package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.domain.video.Resource;

import java.util.Set;

public record CreateVideoCommand(
        String title,
        String description,
        int launchedAt,
        double duration,
        boolean opened,
        boolean published,
        String rating,
        Set<String> categories,
        Set<String> genres,
        Set<String> members,
        Resource video,
        Resource trailer,
        Resource banner,
        Resource thumbnail,
        Resource thumbnailHalf
) {

    public static CreateVideoCommand with(
            final String title,
            final String description,
            final int launchedAt,
            final double duration,
            final boolean opened,
            final boolean published,
            final String rating,
            final Set<String> categories,
            final Set<String> genres,
            final Set<String> members,
            final Resource video,
            final Resource trailer,
            final Resource banner,
            final Resource thumbnail,
            final Resource thumbnailHalf
    ) {
        return new CreateVideoCommand(
                title,
                description,
                launchedAt,
                duration,
                opened,
                published,
                rating,
                categories,
                genres,
                members,
                video,
                trailer,
                banner,
                thumbnail,
                thumbnailHalf
        );
    }
}
