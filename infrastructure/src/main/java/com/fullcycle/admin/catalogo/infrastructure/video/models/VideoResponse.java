package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

public record VideoResponse(
        @JsonProperty("id") String id,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("year_launched") int yearLaunched,
        @JsonProperty("duration") double duration,
        @JsonProperty("opened") boolean opened,
        @JsonProperty("published") boolean published,
        @JsonProperty("rating") String rating,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        @JsonProperty("banner") ImageMediaResponse banner,
        @JsonProperty("thumbnail") ImageMediaResponse thumbnail,
        @JsonProperty("thumbnail_half") ImageMediaResponse thumbnailHalf,
        @JsonProperty("video") AudioVideoMediaResponse video,
        @JsonProperty("trailer") AudioVideoMediaResponse trailer,
        @JsonProperty("categories_id") Set<String> categoriesId,
        @JsonProperty("genres_id") Set<String> genresId,
        @JsonProperty("cast_members_id") Set<String> castMembersId
) {
}
