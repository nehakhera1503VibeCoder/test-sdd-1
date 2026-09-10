package com.example.library.repository;

import com.example.library.domain.Member;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> membersById = new ConcurrentHashMap<>();

    @Override
    public Member save(Member member) {
        membersById.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(membersById.get(id));
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return membersById.values().stream()
                .filter(m -> m.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Member> findAll() {
        return List.copyOf(membersById.values());
    }
}
