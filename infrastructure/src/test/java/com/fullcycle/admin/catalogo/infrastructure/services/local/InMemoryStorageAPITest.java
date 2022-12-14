package com.fullcycle.admin.catalogo.infrastructure.services.local;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryStorageAPITest {

    private InMemoryStorageService target = new InMemoryStorageService();

    @BeforeEach
    public void setUp() {
        target.clear();
    }

    @Test
    public void givenValidResource_whenCallsStore_shouldStoreIt() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final var expectedId = "item";

        target.store(expectedId, expectedResource);

        final var actualContent = this.target.storage().get(expectedId);

        Assertions.assertEquals(expectedResource, actualContent);
    }

    @Test
    public void givenResource_whenCallsGet_shouldRetrieveIt() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final var expectedId = "item";

        this.target.storage().put(expectedId, expectedResource);

        final var actualContent = target.get(expectedId).get();

        Assertions.assertEquals(expectedResource, actualContent);
    }

    @Test
    public void givenInvalidResource_whenCallsGet_shouldRetrieveEmpty() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
        final var expectedId = "jajaja";

        this.target.storage().put("item", expectedResource);

        final var actualContent = target.get(expectedId);

        Assertions.assertTrue(actualContent.isEmpty());
    }

    @Test
    public void givenPrefix_whenCallsList_shouldRetrieveAll() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);

        final var expectedIds = List.of("item1", "item2");

        this.target.storage().put("item1", expectedResource);
        this.target.storage().put("item2", expectedResource);

        final var actualContent = target.list("it");

        Assertions.assertTrue(
                expectedIds.size() == actualContent.size()
                        && expectedIds.containsAll(actualContent)
        );
    }

    @Test
    public void givenResource_whenCallsDeleteAll_shouldEmptyStorage() {
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);

        final var expectedIds = List.of("item1", "item2");

        this.target.storage().put("item1", expectedResource);
        this.target.storage().put("item2", expectedResource);

        target.deleteAll(expectedIds);

        Assertions.assertTrue(this.target.storage().isEmpty());
    }
}