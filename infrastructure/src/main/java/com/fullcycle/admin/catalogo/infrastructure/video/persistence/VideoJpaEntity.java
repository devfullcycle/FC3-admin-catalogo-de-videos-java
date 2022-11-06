package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.video.Rating;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

import javax.persistence.*;
import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Table(name = "videos")
@Entity(name = "Video")
public class VideoJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 4000)
    private String description;

    @Column(name = "year_launched", nullable = false)
    private int yearLaunched;

    @Column(name = "opened", nullable = false)
    private boolean opened;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "rating")
    private Rating rating;

    @Column(name = "duration", precision = 2)
    private double duration;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME(6)")
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private AudioVideoMediaJpaEntity video;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "trailer_id")
    private AudioVideoMediaJpaEntity trailer;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "banner_id")
    private ImageMediaJpaEntity banner;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private ImageMediaJpaEntity thumbnail;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_half_id")
    private ImageMediaJpaEntity thumbnailHalf;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCategoryJpaEntity> categories;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoGenreJpaEntity> genres;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCastMemberJpaEntity> castMembers;

    public VideoJpaEntity() {
    }

    private VideoJpaEntity(
            final String id,
            final String title,
            final String description,
            final int yearLaunched,
            final boolean opened,
            final boolean published,
            final Rating rating,
            final double duration,
            final Instant createdAt,
            final Instant updatedAt,
            final AudioVideoMediaJpaEntity video,
            final AudioVideoMediaJpaEntity trailer,
            final ImageMediaJpaEntity banner,
            final ImageMediaJpaEntity thumbnail,
            final ImageMediaJpaEntity thumbnailHalf
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.yearLaunched = yearLaunched;
        this.opened = opened;
        this.published = published;
        this.rating = rating;
        this.duration = duration;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.video = video;
        this.trailer = trailer;
        this.banner = banner;
        this.thumbnail = thumbnail;
        this.thumbnailHalf = thumbnailHalf;
        this.categories = new HashSet<>(3);
        this.genres = new HashSet<>(3);
        this.castMembers = new HashSet<>(3);
    }

    public static VideoJpaEntity from(final Video aVideo) {
        final var entity = new VideoJpaEntity(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt().getValue(),
                aVideo.getOpened(),
                aVideo.getPublished(),
                aVideo.getRating(),
                aVideo.getDuration(),
                aVideo.getCreatedAt(),
                aVideo.getUpdatedAt(),
                aVideo.getVideo()
                        .map(AudioVideoMediaJpaEntity::from)
                        .orElse(null),
                aVideo.getTrailer()
                        .map(AudioVideoMediaJpaEntity::from)
                        .orElse(null),
                aVideo.getBanner()
                        .map(ImageMediaJpaEntity::from)
                        .orElse(null),
                aVideo.getThumbnail()
                        .map(ImageMediaJpaEntity::from)
                        .orElse(null),
                aVideo.getThumbnailHalf()
                        .map(ImageMediaJpaEntity::from)
                        .orElse(null)
        );

        aVideo.getCategories()
                .forEach(entity::addCategory);

        aVideo.getGenres()
                .forEach(entity::addGenre);

        aVideo.getCastMembers()
                .forEach(entity::addCastMember);

        return entity;
    }

    public Video toAggregate() {
        return Video.with(
                VideoID.from(getId()),
                getTitle(),
                getDescription(),
                Year.of(getYearLaunched()),
                getDuration(),
                isOpened(),
                isPublished(),
                getRating(),
                getCreatedAt(),
                getUpdatedAt(),
                Optional.ofNullable(getBanner())
                        .map(ImageMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getThumbnail())
                        .map(ImageMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getThumbnailHalf())
                        .map(ImageMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getTrailer())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null),
                Optional.ofNullable(getVideo())
                        .map(AudioVideoMediaJpaEntity::toDomain)
                        .orElse(null),
                getCategories().stream()
                        .map(it -> CategoryID.from(it.getId().getCategoryId()))
                        .collect(Collectors.toSet()),
                getGenres().stream()
                        .map(it -> GenreID.from(it.getId().getGenreId()))
                        .collect(Collectors.toSet()),
                getCastMembers().stream()
                        .map(it -> CastMemberID.from(it.getId().getCastMemberId()))
                        .collect(Collectors.toSet())
        );
    }

    public void addCategory(final CategoryID anId) {
        this.categories.add(VideoCategoryJpaEntity.from(this, anId));
    }

    public void addGenre(final GenreID anId) {
        this.genres.add(VideoGenreJpaEntity.from(this, anId));
    }

    public void addCastMember(final CastMemberID anId) {
        this.castMembers.add(VideoCastMemberJpaEntity.from(this, anId));
    }

    public String getId() {
        return id;
    }

    public VideoJpaEntity setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VideoJpaEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VideoJpaEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getYearLaunched() {
        return yearLaunched;
    }

    public VideoJpaEntity setYearLaunched(int yearLaunched) {
        this.yearLaunched = yearLaunched;
        return this;
    }

    public boolean isOpened() {
        return opened;
    }

    public VideoJpaEntity setOpened(boolean opened) {
        this.opened = opened;
        return this;
    }

    public boolean isPublished() {
        return published;
    }

    public VideoJpaEntity setPublished(boolean published) {
        this.published = published;
        return this;
    }

    public Rating getRating() {
        return rating;
    }

    public VideoJpaEntity setRating(Rating rating) {
        this.rating = rating;
        return this;
    }

    public double getDuration() {
        return duration;
    }

    public VideoJpaEntity setDuration(double duration) {
        this.duration = duration;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public VideoJpaEntity setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public VideoJpaEntity setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public VideoJpaEntity setVideo(AudioVideoMediaJpaEntity video) {
        this.video = video;
        return this;
    }

    public AudioVideoMediaJpaEntity getTrailer() {
        return trailer;
    }

    public VideoJpaEntity setTrailer(AudioVideoMediaJpaEntity trailer) {
        this.trailer = trailer;
        return this;
    }

    public ImageMediaJpaEntity getBanner() {
        return banner;
    }

    public VideoJpaEntity setBanner(ImageMediaJpaEntity banner) {
        this.banner = banner;
        return this;
    }

    public ImageMediaJpaEntity getThumbnail() {
        return thumbnail;
    }

    public VideoJpaEntity setThumbnail(ImageMediaJpaEntity thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public ImageMediaJpaEntity getThumbnailHalf() {
        return thumbnailHalf;
    }

    public VideoJpaEntity setThumbnailHalf(ImageMediaJpaEntity thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        return this;
    }

    public Set<VideoCategoryJpaEntity> getCategories() {
        return categories;
    }

    public VideoJpaEntity setCategories(Set<VideoCategoryJpaEntity> categories) {
        this.categories = categories;
        return this;
    }

    public Set<VideoGenreJpaEntity> getGenres() {
        return genres;
    }

    public VideoJpaEntity setGenres(Set<VideoGenreJpaEntity> genres) {
        this.genres = genres;
        return this;
    }

    public Set<VideoCastMemberJpaEntity> getCastMembers() {
        return castMembers;
    }

    public VideoJpaEntity setCastMembers(Set<VideoCastMemberJpaEntity> castMembers) {
        this.castMembers = castMembers;
        return this;
    }

    public Set<CategoryID> getCategoriesID() {
        return CollectionUtils.mapTo(getCategories(), it -> CategoryID.from(it.getId().getCategoryId()));
    }

    public Set<GenreID> getGenresID() {
        return CollectionUtils.mapTo(getGenres(), it -> GenreID.from(it.getId().getGenreId()));
    }

    public Set<CastMemberID> getCastMembersID() {
        return CollectionUtils.mapTo(getCastMembers(), it -> CastMemberID.from(it.getId().getCastMemberId()));
    }
}
