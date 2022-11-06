
CREATE TABLE genres (
    id CHAR(32) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    deleted_at DATETIME(6) NULL
);

CREATE TABLE genres_categories (
    genre_id CHAR(32) NOT NULL,
    category_id CHAR(32) NOT NULL,
    CONSTRAINT idx_genre_category UNIQUE (genre_id, category_id),
    CONSTRAINT fk_genre_id FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE,
    CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);