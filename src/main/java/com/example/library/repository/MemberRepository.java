package com.example.library.repository;

import com.example.library.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(String id);

    Optional<Member> findByEmail(String email);

    List<Member> findAll();
}
