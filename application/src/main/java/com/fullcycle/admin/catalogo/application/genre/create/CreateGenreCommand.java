package com.fullcycle.admin.catalogo.application.genre.create;

import java.util.List;

public record CreateGenreCommand(
        String name,
        boolean isActive,
        List<String> categories
) {

    public static CreateGenreCommand with(
            final String aName,
            final Boolean isActive,
            final List<String> categories
    ) {
        return new CreateGenreCommand(aName, isActive != null ? isActive : true, categories);
    }
}
