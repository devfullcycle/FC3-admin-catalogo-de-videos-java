package com.fullcycle.admin.catalogo.infrastructure.genre.persistence;

import com.fullcycle.admin.catalogo.domain.category.CategoryID;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "genres_categories")
public class GenreCategoryJpaEntity {

    @EmbeddedId
    private GenreCategoryID id;

    @ManyToOne
    @MapsId("genreId")
    private GenreJpaEntity genre;

    public GenreCategoryJpaEntity() {}

    private GenreCategoryJpaEntity(final GenreJpaEntity aGenre, final CategoryID aCategoryId) {
        this.id = GenreCategoryID.from(aGenre.getId(), aCategoryId.getValue());
        this.genre = aGenre;
    }

    public static GenreCategoryJpaEntity from(final GenreJpaEntity aGenre, final CategoryID aCategoryId) {
        return new GenreCategoryJpaEntity(aGenre, aCategoryId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GenreCategoryJpaEntity that = (GenreCategoryJpaEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public GenreCategoryID getId() {
        return id;
    }

    public GenreCategoryJpaEntity setId(GenreCategoryID id) {
        this.id = id;
        return this;
    }

    public GenreJpaEntity getGenre() {
        return genre;
    }

    public GenreCategoryJpaEntity setGenre(GenreJpaEntity genre) {
        this.genre = genre;
        return this;
    }
}
