package com.fullcycle.admin.catalogo.infrastructure.configuration;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private static final String ROLE_ADMIN = "CATALOGO_ADMIN";
    private static final String ROLE_CAST_MEMBERS = "CATALOGO_CAST_MEMBERS";
    private static final String ROLE_CATEGORIES = "CATALOGO_CATEGORIES";
    private static final String ROLE_GENRES = "CATALOGO_GENRES";
    private static final String ROLE_VIDEOS = "CATALOGO_VIDEOS";

    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> {
                    csrf.disable();
                })
                .authorizeHttpRequests(authorize -> {
                    authorize
                            .antMatchers("/cast_members*").hasAnyRole(ROLE_ADMIN, ROLE_CAST_MEMBERS)
                            .antMatchers("/categories*").hasAnyRole(ROLE_ADMIN, ROLE_CATEGORIES)
                            .antMatchers("/genres*").hasAnyRole(ROLE_ADMIN, ROLE_GENRES)
                            .antMatchers("/videos*").hasAnyRole(ROLE_ADMIN, ROLE_VIDEOS)
                            .anyRequest().hasRole(ROLE_ADMIN);
                })
                .oauth2ResourceServer(oauth -> {
                    oauth.jwt()
                            .jwtAuthenticationConverter(new KeycloakJwtConverter());
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .headers(headers -> {
                    headers.frameOptions().sameOrigin();
                })
                .build();
    }

    static class KeycloakJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        private final KeycloakAuthoritiesConverter authoritiesConverter;

        public KeycloakJwtConverter() {
            this.authoritiesConverter = new KeycloakAuthoritiesConverter();
        }

        @Override
        public AbstractAuthenticationToken convert(final Jwt jwt) {
            return new JwtAuthenticationToken(jwt, extractAuthorities(jwt), extractPrincipal(jwt));
        }

        private String extractPrincipal(final Jwt jwt) {
            return jwt.getClaimAsString(JwtClaimNames.SUB);
        }

        private Collection<? extends GrantedAuthority> extractAuthorities(final Jwt jwt) {
            return this.authoritiesConverter.convert(jwt);
        }
    }

    static class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        private static final String REALM_ACCESS = "realm_access";
        private static final String ROLES = "roles";
        private static final String RESOURCE_ACCESS = "resource_access";
        private static final String SEPARATOR = "_";

        @Override
        public Collection<GrantedAuthority> convert(final Jwt jwt) {
            final var realmRoles = extractRealmRoles(jwt);
            final var resourceRoles = extractResourceRoles(jwt);

            return Stream.concat(realmRoles, resourceRoles)
                    .map(role -> new SimpleGrantedAuthority(role.toUpperCase()))
                    .collect(Collectors.toSet());
        }

        private Stream<String> extractResourceRoles(final Jwt jwt) {

            final Function<Map.Entry<String, Object>, Stream<String>> mapResource =
                    resource -> {
                        final var key = resource.getKey();
                        final var value = (JSONObject) resource.getValue();
                        final var roles = (Collection<String>) value.get(ROLES);
                        return roles.stream().map(role -> key.concat(SEPARATOR).concat(role));
                    };

            final Function<Set<Map.Entry<String, Object>>, Collection<String>> mapResources =
                    resources -> resources.stream()
                            .flatMap(mapResource)
                            .toList();

            return Optional.ofNullable(jwt.getClaimAsMap(RESOURCE_ACCESS))
                    .map(resources -> resources.entrySet())
                    .map(mapResources)
                    .orElse(Collections.emptyList())
                    .stream();
        }

        private Stream<String> extractRealmRoles(final Jwt jwt) {
            return Optional.ofNullable(jwt.getClaimAsMap(REALM_ACCESS))
                    .map(resource -> (Collection<String>) resource.get(ROLES))
                    .orElse(Collections.emptyList())
                    .stream();
        }
    }
}
