package com.back.domain.member.member.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.repository.MemberRepository;
import com.back.global.globalExceptionHandler.MemberDuplicateUsernameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Optional<Member> findById(int id) {
        return memberRepository.findById(id);
    }

    public Member join(String username, String password, String name) {
        if (memberRepository.findByUsername(username).isPresent()) {
            throw new MemberDuplicateUsernameException(username);
        }
        return memberRepository.save(new Member(username, password, name));
    }

    public Optional<Member> findLatest() {
        return memberRepository.findFirstByOrderByIdDesc();
    }
}
