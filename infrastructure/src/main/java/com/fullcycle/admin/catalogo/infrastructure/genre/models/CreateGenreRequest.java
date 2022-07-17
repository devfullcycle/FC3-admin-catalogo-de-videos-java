package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public record CreateGenreRequest(
        @JsonProperty("name") String name,
        @JsonProperty("categories_id") List<String> categories,
        @JsonProperty("is_active") Boolean active
) {

    public boolean isActive() {
        return this.active != null ? this.active : true;
    }

    public List<String> categories() {
        return this.categories != null ? this.categories : Collections.emptyList();
    }
}
