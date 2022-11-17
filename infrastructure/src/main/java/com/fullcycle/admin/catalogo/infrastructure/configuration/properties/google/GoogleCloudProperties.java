package com.fullcycle.admin.catalogo.infrastructure.configuration.properties.google;

public class GoogleCloudProperties {

    private String credentials;

    private String projectId;

    public GoogleCloudProperties() {
    }

    public String getCredentials() {
        return credentials;
    }

    public GoogleCloudProperties setCredentials(String credentials) {
        this.credentials = credentials;
        return this;
    }

    public String getProjectId() {
        return projectId;
    }

    public GoogleCloudProperties setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }
}
