package com.back.global.initData;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.domain.post.post.entitty.Post;
import com.back.domain.post.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    @Lazy
    @Autowired
    private BaseInitData self;

    private final PostService postService;
    @Autowired
    private MemberService memberService;

    @Bean
    ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        if (postService.count() > 0) return;

        Member system = memberService.join("system", "12345678", "system");
        Member admin = memberService.join("admin", "12345678", "admin");
        Member user1 = memberService.join("user1", "12345678", "user1");
        Member user2 = memberService.join("user2", "12345678", "user2");
        Member user3 = memberService.join("user3", "12345678", "user3");
        Member user4 = memberService.join("user4", "12345678", "user4");

        Post post1 = postService.write(user1, "제목 1", "내용 1");
        Post post2 = postService.write(user2, "제목 2", "내용 2");
        Post post3 = postService.write(user3, "제목 3", "내용 3");

        post1.addComment(user1, "댓글 1-1");
        post1.addComment(user2, "댓글 1-2");
        post1.addComment(user3, "댓글 1-3");
        post2.addComment(user1, "댓글 2-1");
        post2.addComment(user2, "댓글 2-2");
    }

}
