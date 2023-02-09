package com.fullcycle.admin.catalogo;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public interface ApiTest {

    JwtRequestPostProcessor ADMIN_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_ADMIN"));

    JwtRequestPostProcessor CATEGORIES_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_CATEGORIES"));

    JwtRequestPostProcessor CAST_MEMBERS_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_CAST_MEMBERS"));

    JwtRequestPostProcessor GENRES_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_GENRES"));

    JwtRequestPostProcessor VIDEOS_JWT =
            jwt().authorities(new SimpleGrantedAuthority("ROLE_CATALOGO_VIDEOS"));
}
