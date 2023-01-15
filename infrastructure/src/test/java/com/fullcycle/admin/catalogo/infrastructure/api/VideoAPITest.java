package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.MediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.upload.UploadMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.VideoOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.ListVideosUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.VideoListOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoUseCase;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = VideoAPI.class)
public class VideoAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateVideoUseCase createVideoUseCase;

    @MockBean
    private GetVideoByIdUseCase getVideoByIdUseCase;

    @MockBean
    private UpdateVideoUseCase updateVideoUseCase;

    @MockBean
    private DeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private ListVideosUseCase listVideosUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @MockBean
    private UploadMediaUseCase uploadMediaUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateFull_shouldReturnAnId() throws Exception {
        // given
        final var wesley = Fixture.CastMembers.wesley();
        final var aulas = Fixture.Categories.aulas();
        final var tech = Fixture.Genres.tech();

        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var expectedVideo =
                new MockMultipartFile("video_file", "video.mp4", "video/mp4", "VIDEO".getBytes());

        final var expectedTrailer =
                new MockMultipartFile("trailer_file", "trailer.mp4", "video/mp4", "TRAILER".getBytes());

        final var expectedBanner =
                new MockMultipartFile("banner_file", "banner.jpg", "image/jpg", "BANNER".getBytes());

        final var expectedThumb =
                new MockMultipartFile("thumb_file", "thumbnail.jpg", "image/jpg", "THUMB".getBytes());

        final var expectedThumbHalf =
                new MockMultipartFile("thumb_half_file", "thumbnailHalf.jpg", "image/jpg", "THUMBHALF".getBytes());

        when(createVideoUseCase.execute(any()))
                .thenReturn(new CreateVideoOutput(expectedId.getValue()));

        // when
        final var aRequest = multipart("/videos")
                .file(expectedVideo)
                .file(expectedTrailer)
                .file(expectedBanner)
                .file(expectedThumb)
                .file(expectedThumbHalf)
                .param("title", expectedTitle)
                .param("description", expectedDescription)
                .param("year_launched", String.valueOf(expectedLaunchYear.getValue()))
                .param("duration", expectedDuration.toString())
                .param("opened", String.valueOf(expectedOpened))
                .param("published", String.valueOf(expectedPublished))
                .param("rating", expectedRating.getName())
                .param("cast_members_id", wesley.getId().getValue())
                .param("categories_id", aulas.getId().getValue())
                .param("genres_id", tech.getId().getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        this.mvc.perform(aRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualCmd.title());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), actualCmd.launchedAt());
        Assertions.assertEquals(expectedDuration, actualCmd.duration());
        Assertions.assertEquals(expectedOpened, actualCmd.opened());
        Assertions.assertEquals(expectedPublished, actualCmd.published());
        Assertions.assertEquals(expectedRating.getName(), actualCmd.rating());
        Assertions.assertEquals(expectedCategories, actualCmd.categories());
        Assertions.assertEquals(expectedGenres, actualCmd.genres());
        Assertions.assertEquals(expectedMembers, actualCmd.members());
        Assertions.assertEquals(expectedVideo.getOriginalFilename(), actualCmd.getVideo().get().name());
        Assertions.assertEquals(expectedTrailer.getOriginalFilename(), actualCmd.getTrailer().get().name());
        Assertions.assertEquals(expectedBanner.getOriginalFilename(), actualCmd.getBanner().get().name());
        Assertions.assertEquals(expectedThumb.getOriginalFilename(), actualCmd.getThumbnail().get().name());
        Assertions.assertEquals(expectedThumbHalf.getOriginalFilename(), actualCmd.getThumbnailHalf().get().name());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreateFull_shouldReturnError() throws Exception {
        // given
        final var expectedErrorMessage = "title is required";

        when(createVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = multipart("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsCreatePartial_shouldReturnId() throws Exception {
        // given
        final var wesley = Fixture.CastMembers.wesley();
        final var aulas = Fixture.Categories.aulas();
        final var tech = Fixture.Genres.tech();

        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var aCmd = new CreateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        when(createVideoUseCase.execute(any()))
                .thenReturn(new CreateVideoOutput(expectedId.getValue()));

        // when

        final var aRequest = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

        verify(createVideoUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualCmd.title());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), actualCmd.launchedAt());
        Assertions.assertEquals(expectedDuration, actualCmd.duration());
        Assertions.assertEquals(expectedOpened, actualCmd.opened());
        Assertions.assertEquals(expectedPublished, actualCmd.published());
        Assertions.assertEquals(expectedRating.getName(), actualCmd.rating());
        Assertions.assertEquals(expectedCategories, actualCmd.categories());
        Assertions.assertEquals(expectedGenres, actualCmd.genres());
        Assertions.assertEquals(expectedMembers, actualCmd.members());
        Assertions.assertTrue(actualCmd.getVideo().isEmpty());
        Assertions.assertTrue(actualCmd.getTrailer().isEmpty());
        Assertions.assertTrue(actualCmd.getBanner().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnail().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnailHalf().isEmpty());
    }

    @Test
    public void givenAnEmptyBody_whenCallsCreatePartial_shouldReturnError() throws Exception {
        // when
        final var aRequest = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isBadRequest());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsCreatePartial_shouldReturnError() throws Exception {
        // given
        final var expectedErrorMessage = "title is required";

        when(createVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when
        final var aRequest = post("/videos")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "Olá Mundo!"
                        }
                        """);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnVideo() throws Exception {
        // given
        final var wesley = Fixture.CastMembers.wesley();
        final var aulas = Fixture.Categories.aulas();
        final var tech = Fixture.Genres.tech();

        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var expectedVideo = Fixture.Videos.audioVideo(VideoMediaType.VIDEO);
        final var expectedTrailer = Fixture.Videos.audioVideo(VideoMediaType.TRAILER);
        final var expectedBanner = Fixture.Videos.image(VideoMediaType.BANNER);
        final var expectedThumb = Fixture.Videos.image(VideoMediaType.THUMBNAIL);
        final var expectedThumbHalf = Fixture.Videos.image(VideoMediaType.THUMBNAIL_HALF);

        final var aVideo = Video.newVideo(
                        expectedTitle,
                        expectedDescription,
                        expectedLaunchYear,
                        expectedDuration,
                        expectedOpened,
                        expectedPublished,
                        expectedRating,
                        mapTo(expectedCategories, CategoryID::from),
                        mapTo(expectedGenres, GenreID::from),
                        mapTo(expectedMembers, CastMemberID::from)
                )
                .updateVideoMedia(expectedVideo)
                .updateTrailerMedia(expectedTrailer)
                .updateBannerMedia(expectedBanner)
                .updateThumbnailMedia(expectedThumb)
                .updateThumbnailHalfMedia(expectedThumbHalf);

        final var expectedId = aVideo.getId().getValue();

        when(getVideoByIdUseCase.execute(any()))
                .thenReturn(VideoOutput.from(aVideo));

        // when
        final var aRequest = get("/videos/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId)))
                .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                .andExpect(jsonPath("$.year_launched", equalTo(expectedLaunchYear.getValue())))
                .andExpect(jsonPath("$.duration", equalTo(expectedDuration)))
                .andExpect(jsonPath("$.opened", equalTo(expectedOpened)))
                .andExpect(jsonPath("$.published", equalTo(expectedPublished)))
                .andExpect(jsonPath("$.rating", equalTo(expectedRating.getName())))
                .andExpect(jsonPath("$.created_at", equalTo(aVideo.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at", equalTo(aVideo.getUpdatedAt().toString())))
                .andExpect(jsonPath("$.banner.id", equalTo(expectedBanner.id())))
                .andExpect(jsonPath("$.banner.name", equalTo(expectedBanner.name())))
                .andExpect(jsonPath("$.banner.location", equalTo(expectedBanner.location())))
                .andExpect(jsonPath("$.banner.checksum", equalTo(expectedBanner.checksum())))
                .andExpect(jsonPath("$.thumbnail.id", equalTo(expectedThumb.id())))
                .andExpect(jsonPath("$.thumbnail.name", equalTo(expectedThumb.name())))
                .andExpect(jsonPath("$.thumbnail.location", equalTo(expectedThumb.location())))
                .andExpect(jsonPath("$.thumbnail.checksum", equalTo(expectedThumb.checksum())))
                .andExpect(jsonPath("$.thumbnail_half.id", equalTo(expectedThumbHalf.id())))
                .andExpect(jsonPath("$.thumbnail_half.name", equalTo(expectedThumbHalf.name())))
                .andExpect(jsonPath("$.thumbnail_half.location", equalTo(expectedThumbHalf.location())))
                .andExpect(jsonPath("$.thumbnail_half.checksum", equalTo(expectedThumbHalf.checksum())))
                .andExpect(jsonPath("$.video.id", equalTo(expectedVideo.id())))
                .andExpect(jsonPath("$.video.name", equalTo(expectedVideo.name())))
                .andExpect(jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())))
                .andExpect(jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())))
                .andExpect(jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())))
                .andExpect(jsonPath("$.video.status", equalTo(expectedVideo.status().name())))
                .andExpect(jsonPath("$.trailer.id", equalTo(expectedTrailer.id())))
                .andExpect(jsonPath("$.trailer.name", equalTo(expectedTrailer.name())))
                .andExpect(jsonPath("$.trailer.checksum", equalTo(expectedTrailer.checksum())))
                .andExpect(jsonPath("$.trailer.location", equalTo(expectedTrailer.rawLocation())))
                .andExpect(jsonPath("$.trailer.encoded_location", equalTo(expectedTrailer.encodedLocation())))
                .andExpect(jsonPath("$.trailer.status", equalTo(expectedTrailer.status().name())))
                .andExpect(jsonPath("$.categories_id", equalTo(new ArrayList(expectedCategories))))
                .andExpect(jsonPath("$.genres_id", equalTo(new ArrayList(expectedGenres))))
                .andExpect(jsonPath("$.cast_members_id", equalTo(new ArrayList(expectedMembers))));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetById_shouldReturnNotFound() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "Video with ID %s was not found".formatted(expectedId.getValue());

        when(getVideoByIdUseCase.execute(any()))
                .thenThrow(NotFoundException.with(Video.class, expectedId));

        // when
        final var aRequest = get("/videos/{id}", expectedId)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isNotFound())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateVideo_shouldReturnVideoId() throws Exception {
        // given
        final var wesley = Fixture.CastMembers.wesley();
        final var aulas = Fixture.Categories.aulas();
        final var tech = Fixture.Genres.tech();

        final var expectedId = VideoID.unique();
        final var expectedTitle = Fixture.title();
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var aCmd = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        when(updateVideoUseCase.execute(any()))
                .thenReturn(new UpdateVideoOutput(expectedId.getValue()));

        // when

        final var aRequest = put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCmd));

        this.mvc.perform(aRequest)
                .andExpect(status().isOk())
                .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

        // then
        final var cmdCaptor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

        verify(updateVideoUseCase).execute(cmdCaptor.capture());

        final var actualCmd = cmdCaptor.getValue();

        Assertions.assertEquals(expectedTitle, actualCmd.title());
        Assertions.assertEquals(expectedDescription, actualCmd.description());
        Assertions.assertEquals(expectedLaunchYear.getValue(), actualCmd.launchedAt());
        Assertions.assertEquals(expectedDuration, actualCmd.duration());
        Assertions.assertEquals(expectedOpened, actualCmd.opened());
        Assertions.assertEquals(expectedPublished, actualCmd.published());
        Assertions.assertEquals(expectedRating.getName(), actualCmd.rating());
        Assertions.assertEquals(expectedCategories, actualCmd.categories());
        Assertions.assertEquals(expectedGenres, actualCmd.genres());
        Assertions.assertEquals(expectedMembers, actualCmd.members());
        Assertions.assertTrue(actualCmd.getVideo().isEmpty());
        Assertions.assertTrue(actualCmd.getTrailer().isEmpty());
        Assertions.assertTrue(actualCmd.getBanner().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnail().isEmpty());
        Assertions.assertTrue(actualCmd.getThumbnailHalf().isEmpty());
    }

    @Test
    public void givenAnInvalidCommand_whenCallsUpdateVideo_shouldReturnNotification() throws Exception {
        // given
        final var wesley = Fixture.CastMembers.wesley();
        final var aulas = Fixture.Categories.aulas();
        final var tech = Fixture.Genres.tech();

        final var expectedId = VideoID.unique();
        final var expectedErrorMessage = "'title' should not be empty";
        final var expectedErrorCount = 1;

        final var expectedTitle = "";
        final var expectedDescription = Fixture.Videos.description();
        final var expectedLaunchYear = Year.of(Fixture.year());
        final var expectedDuration = Fixture.duration();
        final var expectedOpened = Fixture.bool();
        final var expectedPublished = Fixture.bool();
        final var expectedRating = Fixture.Videos.rating();
        final var expectedCategories = Set.of(aulas.getId().getValue());
        final var expectedGenres = Set.of(tech.getId().getValue());
        final var expectedMembers = Set.of(wesley.getId().getValue());

        final var aCmd = new UpdateVideoRequest(
                expectedTitle,
                expectedDescription,
                expectedDuration,
                expectedLaunchYear.getValue(),
                expectedOpened,
                expectedPublished,
                expectedRating.getName(),
                expectedMembers,
                expectedCategories,
                expectedGenres
        );

        when(updateVideoUseCase.execute(any()))
                .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

        // when

        final var aRequest = put("/videos/{id}", expectedId.getValue())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCmd));

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

        verify(updateVideoUseCase).execute(any());
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        // given
        final var expectedId = VideoID.unique();

        doNothing().when(deleteVideoUseCase).execute(any());

        // when
        final var aRequest = delete("/videos/{id}", expectedId.getValue());

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isNoContent());

        verify(deleteVideoUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenValidParams_whenCallsListVideos_shouldReturnPagination() throws Exception {
        // given
        final var aVideo = new VideoPreview(Fixture.video());

        final var expectedPage = 50;
        final var expectedPerPage = 50;
        final var expectedTerms = "Algo";
        final var expectedSort = "title";
        final var expectedDirection = "asc";
        final var expectedCastMembers = "cast1";
        final var expectedGenres = "gen1";
        final var expectedCategories = "cat1";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(listVideosUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/videos")
                .queryParam("page", String.valueOf(expectedPage))
                .queryParam("perPage", String.valueOf(expectedPerPage))
                .queryParam("sort", expectedSort)
                .queryParam("dir", expectedDirection)
                .queryParam("search", expectedTerms)
                .queryParam("cast_members_ids", expectedCastMembers)
                .queryParam("categories_ids", expectedCategories)
                .queryParam("genres_ids", expectedGenres)
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideosUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertEquals(Set.of(CategoryID.from(expectedCategories)), actualQuery.categories());
        Assertions.assertEquals(Set.of(CastMemberID.from(expectedCastMembers)), actualQuery.castMembers());
        Assertions.assertEquals(Set.of(GenreID.from(expectedGenres)), actualQuery.genres());
    }

    @Test
    public void givenEmptyParams_whenCallsListVideosWithDefaultValues_shouldReturnPagination() throws Exception {
        // given
        final var aVideo = new VideoPreview(Fixture.video());

        final var expectedPage = 0;
        final var expectedPerPage = 25;
        final var expectedTerms = "";
        final var expectedSort = "title";
        final var expectedDirection = "asc";

        final var expectedItemsCount = 1;
        final var expectedTotal = 1;

        final var expectedItems = List.of(VideoListOutput.from(aVideo));

        when(listVideosUseCase.execute(any()))
                .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

        // when
        final var aRequest = get("/videos")
                .accept(MediaType.APPLICATION_JSON);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id", equalTo(aVideo.id())))
                .andExpect(jsonPath("$.items[0].title", equalTo(aVideo.title())))
                .andExpect(jsonPath("$.items[0].description", equalTo(aVideo.description())))
                .andExpect(jsonPath("$.items[0].created_at", equalTo(aVideo.createdAt().toString())))
                .andExpect(jsonPath("$.items[0].updated_at", equalTo(aVideo.updatedAt().toString())));

        final var captor = ArgumentCaptor.forClass(VideoSearchQuery.class);

        verify(listVideosUseCase).execute(captor.capture());

        final var actualQuery = captor.getValue();
        Assertions.assertEquals(expectedPage, actualQuery.page());
        Assertions.assertEquals(expectedPerPage, actualQuery.perPage());
        Assertions.assertEquals(expectedDirection, actualQuery.direction());
        Assertions.assertEquals(expectedSort, actualQuery.sort());
        Assertions.assertEquals(expectedTerms, actualQuery.terms());
        Assertions.assertTrue(actualQuery.categories().isEmpty());
        Assertions.assertTrue(actualQuery.castMembers().isEmpty());
        Assertions.assertTrue(actualQuery.genres().isEmpty());
    }

    @Test
    public void givenAValidVideoIdAndFileType_whenCallsGetMediaById_shouldReturnContent() throws Exception {
        // given
        final var expectedId = VideoID.unique();

        final var expectedMediaType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedMediaType);

        final var expectedMedia = new MediaOutput(expectedResource.content(), expectedResource.contentType(), expectedResource.name());

        when(getMediaUseCase.execute(any())).thenReturn(expectedMedia);

        // when
        final var aRequest = get("/videos/{id}/medias/{type}", expectedId.getValue(), expectedMediaType.name());

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isOk())
                .andExpect(header().string(CONTENT_TYPE, expectedMedia.contentType()))
                .andExpect(header().string(CONTENT_LENGTH, String.valueOf(expectedMedia.content().length)))
                .andExpect(header().string(CONTENT_DISPOSITION, "attachment; filename=%s".formatted(expectedMedia.name())))
                .andExpect(content().bytes(expectedMedia.content()));

        final var captor = ArgumentCaptor.forClass(GetMediaCommand.class);

        verify(this.getMediaUseCase).execute(captor.capture());

        final var actualCmd = captor.getValue();
        Assertions.assertEquals(expectedId.getValue(), actualCmd.videoId());
        Assertions.assertEquals(expectedMediaType.name(), actualCmd.mediaType());
    }

    @Test
    public void givenAValidVideoIdAndFile_whenCallsUploadMedia_shouldStoreIt() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedType = VideoMediaType.VIDEO;
        final var expectedResource = Fixture.Videos.resource(expectedType);

        final var expectedVideo =
                new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        when(uploadMediaUseCase.execute(any()))
                .thenReturn(new UploadMediaOutput(expectedId.getValue(), expectedType));

        // when
        final var aRequest = multipart("/videos/{id}/medias/{type}", expectedId.getValue(), expectedType.name())
                .file(expectedVideo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/videos/%s/medias/%s".formatted(expectedId.getValue(), expectedType.name())))
                .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.video_id", equalTo(expectedId.getValue())))
                .andExpect(jsonPath("$.media_type", equalTo(expectedType.name())));

        final var captor = ArgumentCaptor.forClass(UploadMediaCommand.class);

        verify(this.uploadMediaUseCase).execute(captor.capture());

        final var actualCmd = captor.getValue();
        Assertions.assertEquals(expectedId.getValue(), actualCmd.videoId());
        Assertions.assertEquals(expectedResource.content(), actualCmd.videoResource().resource().content());
        Assertions.assertEquals(expectedResource.name(), actualCmd.videoResource().resource().name());
        Assertions.assertEquals(expectedResource.contentType(), actualCmd.videoResource().resource().contentType());
        Assertions.assertEquals(expectedType, actualCmd.videoResource().type());
    }

    @Test
    public void givenAnInvalidMediaType_whenCallsUploadMedia_shouldReturnError() throws Exception {
        // given
        final var expectedId = VideoID.unique();
        final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

        final var expectedVideo =
                new MockMultipartFile("media_file", expectedResource.name(), expectedResource.contentType(), expectedResource.content());

        // when
        final var aRequest = multipart("/videos/{id}/medias/INVALID", expectedId.getValue())
                .file(expectedVideo)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA);

        final var response = this.mvc.perform(aRequest);

        // then
        response.andExpect(status().isUnprocessableEntity())
                .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo("Invalid INVALID for VideoMediaType")));
    }
}