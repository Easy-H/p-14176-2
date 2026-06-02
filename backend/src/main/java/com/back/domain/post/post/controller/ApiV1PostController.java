package com.back.domain.post.post.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.domain.post.post.dto.PostDto;
import com.back.domain.post.post.entitty.Post;
import com.back.domain.post.post.service.PostService;
import com.back.global.globalExceptionHandler.UnauthenticatedException;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Tag(name = "ApiV1PostController", description = "API 글 컨트롤러")
public class ApiV1PostController {

    private final MemberService memberService;
    private final PostService postService;

    @GetMapping
    @Operation(summary = "다건 조회")
    public List<PostDto> getItems() {
        List<Post> items = postService.findAll();

        return items.stream()
                .map(PostDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "단건 조회")
    public PostDto getItem(
            @PathVariable int id
    ) {
        Post item = postService.findById(id).get();

        return new PostDto(item);
    }

    public record PostWriteReqBody (
        @NotBlank
        @Size(min = 2, max = 100)
        String title,
        @NotBlank
        @Size(min = 2, max = 5000)
        String content
    ) {}

    @PostMapping
    @Transactional
    @Operation(summary = "작성")
    public RsData<PostDto> write(
            @RequestBody @Validated PostWriteReqBody form,
            @RequestParam(defaultValue="0") int actorId
    ) {
        Member author = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
        Post post = postService.write(author, form.title, form.content);

        return new RsData<>("201-1",
                "%d번 글이 작성되었습니다.".formatted(post.getId()),
                new PostDto(post));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "수정")
    public RsData<PostDto> modify(
            @PathVariable int id,
            @RequestBody @Validated PostWriteReqBody form,
            @RequestParam(defaultValue="0") int actorId
    ) {
        Member author = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
        Post post = postService.findById(id).get();
        postService.modify(author, post, form.title, form.content);

        return new RsData<>("200-1",
                "%d번 글이 수정되었습니다.".formatted(post.getId()),
                new PostDto(post));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "삭제")
    public RsData<Void> delete(
            @PathVariable int id,
            @RequestParam(defaultValue="0") int actorId
    ) {
        Member author = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
        Post post = postService.findById(id).get();

        postService.delete(author, post);

        return new RsData<>("200-1",
                "%d번 게시물이 삭제되었습니다.".formatted(id));
    }


}
