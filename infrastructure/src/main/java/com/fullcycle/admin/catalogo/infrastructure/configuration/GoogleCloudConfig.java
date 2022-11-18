package com.fullcycle.admin.catalogo.infrastructure.configuration;

import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.google.GoogleCloudProperties;
import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.google.GoogleStorageProperties;
import com.google.api.gax.retrying.RetrySettings;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.http.HttpTransportOptions;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.threeten.bp.Duration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Configuration
@Profile({"development", "production"})
public class GoogleCloudConfig {

    @Bean
    @ConfigurationProperties("google.cloud")
    public GoogleCloudProperties googleCloudProperties() {
        return new GoogleCloudProperties();
    }

    @Bean
    @ConfigurationProperties("google.cloud.storage.catalogo-videos")
    public GoogleStorageProperties googleStorageProperties() {
        return new GoogleStorageProperties();
    }

    @Bean
    public Credentials credentials(final GoogleCloudProperties props) throws IOException {
        final var jsonBin = Base64.getDecoder()
                .decode(Objects.requireNonNull(props.getCredentials()));

        return GoogleCredentials.fromStream(new ByteArrayInputStream(jsonBin));
    }

    @Bean
    public Storage storage(
            final Credentials credentials,
            final GoogleCloudProperties cloudConfig,
            final GoogleStorageProperties storageConfig
    ) {
        final var transportOptions = HttpTransportOptions.newBuilder()
                .setConnectTimeout(storageConfig.getConnectTimeout())
                .setReadTimeout(storageConfig.getReadTimeout())
                .build();

        final var retry = RetrySettings.newBuilder()
                .setInitialRetryDelay(Duration.ofMillis(storageConfig.getRetryDelay()))
                .setMaxRetryDelay(Duration.ofMillis(storageConfig.getRetryMaxDelay()))
                .setMaxAttempts(storageConfig.getRetryMaxAttempts())
                .setRetryDelayMultiplier(storageConfig.getRetryMultiplier())
                .build();

        return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .setProjectId(cloudConfig.getProjectId())
                .setTransportOptions(transportOptions)
                .setRetrySettings(retry)
                .build()
                .getService();
    }
}