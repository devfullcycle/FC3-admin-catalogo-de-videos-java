package com.fullcycle.admin.catalogo.infrastructure.castmember.models;

import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;

public record CreateCastMemberRequest(String name, CastMemberType type) {
}
