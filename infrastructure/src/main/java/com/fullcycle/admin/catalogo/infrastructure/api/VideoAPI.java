package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoListResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RequestMapping(value = "videos")
@Tag(name = "Video")
public interface VideoAPI {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "List all videos paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Videos listed"),
            @ApiResponse(responseCode = "422", description = "A query param was invalid"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    Pagination<VideoListResponse> list(
            @RequestParam(name = "search", required = false, defaultValue = "") String search,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "perPage", required = false, defaultValue = "25") int perPage,
            @RequestParam(name = "sort", required = false, defaultValue = "title") String sort,
            @RequestParam(name = "dir", required = false, defaultValue = "asc") String direction,
            @RequestParam(name = "cast_members_ids", required = false, defaultValue = "") Set<String> castMembers,
            @RequestParam(name = "categories_ids", required = false, defaultValue = "") Set<String> categories,
            @RequestParam(name = "genres_ids", required = false, defaultValue = "") Set<String> genres
    );

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new video with medias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> createFull(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "year_launched", required = false) Integer yearLaunched,
            @RequestParam(name = "duration", required = false) Double duration,
            @RequestParam(name = "opened", required = false) Boolean opened,
            @RequestParam(name = "published", required = false) Boolean published,
            @RequestParam(name = "rating", required = false) String rating,
            @RequestParam(name = "categories_id", required = false) Set<String> categories,
            @RequestParam(name = "cast_members_id", required = false) Set<String> castMembers,
            @RequestParam(name = "genres_id", required = false) Set<String> genres,
            @RequestParam(name = "video_file", required = false) MultipartFile videoFile,
            @RequestParam(name = "trailer_file", required = false) MultipartFile trailerFile,
            @RequestParam(name = "banner_file", required = false) MultipartFile bannerFile,
            @RequestParam(name = "thumb_file", required = false) MultipartFile thumbFile,
            @RequestParam(name = "thumb_half_file", required = false) MultipartFile thumbHalfFile
    );

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Create a new video without medias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> createPartial(@RequestBody CreateVideoRequest payload);

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    VideoResponse getById(@PathVariable(name = "id") String id);

    @PutMapping(
            value = "{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "Update a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video updated successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "422", description = "A validation error was thrown"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> update(
            @PathVariable(name = "id") String id,
            @RequestBody UpdateVideoRequest payload
    );

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a video by it's identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Video deleted"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    void deleteById(@PathVariable(name = "id") String id);

    @GetMapping(value = "{id}/medias/{type}")
    @Operation(summary = "Get a video media by it's type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Media retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Media was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<byte[]> getMediaByType(
            @PathVariable(name = "id") String id,
            @PathVariable(name = "type") String type
    );

    @PostMapping(value = "{id}/medias/{type}")
    @Operation(summary = "Upload a video media by it's type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Media created successfully"),
            @ApiResponse(responseCode = "404", description = "Video was not found"),
            @ApiResponse(responseCode = "500", description = "An internal server error was thrown"),
    })
    ResponseEntity<?> uploadMediaByType(
            @PathVariable(name = "id") String id,
            @PathVariable(name = "type") String type,
            @RequestParam(name = "media_file") MultipartFile media
    );
}
