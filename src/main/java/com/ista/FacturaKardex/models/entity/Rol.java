package com.ista.FacturaKardex.models.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

public enum Rol {
    ADMIN(Collections.singletonList("ROLE_ADMIN")),
    PERSON(Collections.singletonList("ROLE_PERSON")),
    CLIENT(Collections.singletonList("ROLE_CLIENT"));

    private final List<SimpleGrantedAuthority> authorities;

    Rol(List<String> authorities) {
        this.authorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }
}
