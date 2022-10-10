package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class VideoCategoryID implements Serializable {

    @Column(name = "video_id", nullable = false)
    private UUID videoId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    public VideoCategoryID() {
    }

    private VideoCategoryID(final UUID videoId, final UUID categoryId) {
        this.videoId = videoId;
        this.categoryId = categoryId;
    }

    public static VideoCategoryID from(final UUID videoId, final UUID categoryId) {
        return new VideoCategoryID(videoId, categoryId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoCategoryID that = (VideoCategoryID) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getCategoryId(), that.getCategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getCategoryId());
    }

    public UUID getVideoId() {
        return videoId;
    }

    public VideoCategoryID setVideoId(UUID videoId) {
        this.videoId = videoId;
        return this;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public VideoCategoryID setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
        return this;
    }
}
