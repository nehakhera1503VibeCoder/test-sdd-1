package com.example.library.api.dto;

import com.example.library.domain.Member;

public class MemberResponseDto {

    private final String id;
    private final String name;
    private final String email;

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
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
