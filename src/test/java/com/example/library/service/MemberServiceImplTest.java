package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.domain.Member;
import com.example.library.exception.ConflictException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.InMemoryMemberRepository;
import com.example.library.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** FR-3. */
class MemberServiceImplTest {

    private MemberService memberService;

    @BeforeEach
    void setUp() {
        memberService = new MemberServiceImpl(new InMemoryMemberRepository());
    }

    @Test
    void registerMember_createsMember() {
        Member member = memberService.registerMember("Ada Lovelace", "ada@example.com");

        assertThat(member.getId()).isNotBlank();
        assertThat(member.getName()).isEqualTo("Ada Lovelace");
        assertThat(member.getEmail()).isEqualTo("ada@example.com");
    }

    @Test
    void registerMember_rejectsDuplicateEmail() {
        memberService.registerMember("Ada Lovelace", "ada@example.com");

        assertThatThrownBy(() -> memberService.registerMember("Ada L.", "ada@example.com"))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("ada@example.com");
    }

    @Test
    void getMember_unknownId_throwsNotFound() {
        assertThatThrownBy(() -> memberService.getMember("does-not-exist"))
                .isInstanceOf(NotFoundException.class);
    }
}
