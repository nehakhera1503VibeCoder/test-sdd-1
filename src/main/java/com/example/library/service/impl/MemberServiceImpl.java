package com.example.library.service.impl;

import com.example.library.domain.Member;
import com.example.library.exception.ConflictException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.MemberRepository;
import com.example.library.service.MemberService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public Member registerMember(String name, String email) {
        memberRepository.findByEmail(email).ifPresent(existing -> {
            throw new ConflictException("A member with email " + email + " already exists (id=" + existing.getId() + ")");
        });
        Member member = new Member(UUID.randomUUID().toString(), name, email);
        return memberRepository.save(member);
    }

    @Override
    public List<Member> listMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Member getMember(String memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("No member with id " + memberId));
    }
}
