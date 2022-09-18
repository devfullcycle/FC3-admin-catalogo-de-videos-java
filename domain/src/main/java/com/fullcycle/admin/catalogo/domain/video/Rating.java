package com.fullcycle.admin.catalogo.domain.video;

import java.util.Arrays;
import java.util.Optional;

public enum Rating {

    ER("ER"),
    L("L"),
    AGE_10("10"),
    AGE_12("12"),
    AGE_14("14"),
    AGE_16("16"),
    AGE_18("18");

    private final String name;

    Rating(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<Rating> of(final String label) {
        return Arrays.stream(Rating.values())
                .filter(it -> it.name.equalsIgnoreCase(label))
                .findFirst();
    }
}
