package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VideoCastMemberID implements Serializable {

    @Column(name = "video_id", nullable = false)
    private String videoId;

    @Column(name = "cast_member_id", nullable = false)
    private String castMemberId;

    public VideoCastMemberID() {
    }

    private VideoCastMemberID(final String videoId, final String castMemberId) {
        this.videoId = videoId;
        this.castMemberId = castMemberId;
    }

    public static VideoCastMemberID from(final String videoId, final String castMemberId) {
        return new VideoCastMemberID(videoId, castMemberId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoCastMemberID that = (VideoCastMemberID) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getCastMemberId(), that.getCastMemberId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getCastMemberId());
    }

    public String getVideoId() {
        return videoId;
    }

    public VideoCastMemberID setVideoId(String videoId) {
        this.videoId = videoId;
        return this;
    }

    public String getCastMemberId() {
        return castMemberId;
    }

    public VideoCastMemberID setCastMemberId(String castMemberId) {
        this.castMemberId = castMemberId;
        return this;
    }
}
