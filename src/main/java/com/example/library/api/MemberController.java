package com.example.library.api;

import com.example.library.api.dto.MemberRequestDto;
import com.example.library.api.dto.MemberResponseDto;
import com.example.library.domain.Member;
import com.example.library.service.MemberService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** FR-3 (docs/01-po-requirements.md). */
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponseDto registerMember(@Valid @RequestBody MemberRequestDto request) {
        Member member = memberService.registerMember(request.getName(), request.getEmail());
        return new MemberResponseDto(member);
    }

    @GetMapping
    public List<MemberResponseDto> listMembers() {
        return memberService.listMembers().stream().map(MemberResponseDto::new).toList();
    }

    @GetMapping("/{memberId}")
    public MemberResponseDto getMember(@PathVariable String memberId) {
        return new MemberResponseDto(memberService.getMember(memberId));
    }
}
