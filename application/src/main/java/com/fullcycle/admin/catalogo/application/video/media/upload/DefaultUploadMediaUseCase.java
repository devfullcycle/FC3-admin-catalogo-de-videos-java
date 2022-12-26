package com.fullcycle.admin.catalogo.application.video.media.upload;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

import java.util.Objects;

public class DefaultUploadMediaUseCase extends UploadMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;
    private final VideoGateway videoGateway;

    public DefaultUploadMediaUseCase(
            final MediaResourceGateway mediaResourceGateway,
            final VideoGateway videoGateway
    ) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public UploadMediaOutput execute(final UploadMediaCommand aCmd) {
        final var anId = VideoID.from(aCmd.videoId());
        final var aResource = aCmd.videoResource();

        final var aVideo = this.videoGateway.findById(anId)
                .orElseThrow(() -> notFound(anId));

        switch (aResource.type()) {
            case VIDEO -> aVideo.updateVideoMedia(mediaResourceGateway.storeAudioVideo(anId, aResource));
            case TRAILER -> aVideo.updateTrailerMedia(mediaResourceGateway.storeAudioVideo(anId, aResource));
            case BANNER -> aVideo.updateBannerMedia(mediaResourceGateway.storeImage(anId, aResource));
            case THUMBNAIL -> aVideo.updateThumbnailMedia(mediaResourceGateway.storeImage(anId, aResource));
            case THUMBNAIL_HALF -> aVideo.updateThumbnailHalfMedia(mediaResourceGateway.storeImage(anId, aResource));
        }

        return UploadMediaOutput.with(videoGateway.update(aVideo), aResource.type());
    }

    private NotFoundException notFound(final VideoID anId) {
        return NotFoundException.with(Video.class, anId);
    }
}
