package com.back.domain.member.member.controller;

import com.back.domain.member.member.dto.MemberDto;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.global.globalExceptionHandler.UnauthenticatedException;
import com.back.global.rsData.RsData;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/members")
public class ApiV1MemberController {

    private final MemberService memberService;

    public record MemberJoinReqBody (
            @NotBlank
            @Size(min = 4, max = 30)
            String username,
            @NotBlank
            @Size(min = 8, max = 30)
            String password,
            @NotBlank
            @Size(min = 2, max = 30)
            String name
    ) {}

    @PostMapping("/join")
    public RsData<Void> join(
            @RequestBody MemberJoinReqBody form
    ) {
        memberService.join(form.username, form.password, form.name);
        return new RsData<>("201-1", "%s님 환영합니다. 회원가입이 완료되었습니다.".formatted(form.name));
    }

    @GetMapping("/me")
    public MemberDto getMe(
            @RequestParam(defaultValue="0") int actorId
    ) {
        Member member = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);

        return new MemberDto(member);
    }

}
