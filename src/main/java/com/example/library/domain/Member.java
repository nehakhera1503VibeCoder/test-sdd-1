package com.example.library.domain;

import java.util.Objects;

/** A registered library member (FR-3 in docs/01-po-requirements.md). */
public class Member {

    private final String id;
    private final String name;
    private final String email;

    public Member(String id, String name, String email) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.email = Objects.requireNonNull(email, "email");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
