package com.example.library.service;

import com.example.library.domain.Member;
import java.util.List;

public interface MemberService {

    /** FR-3. Rejects a duplicate email with ConflictException. */
    Member registerMember(String name, String email);

    List<Member> listMembers();

    /** Throws NotFoundException if no such member. */
    Member getMember(String memberId);
}
