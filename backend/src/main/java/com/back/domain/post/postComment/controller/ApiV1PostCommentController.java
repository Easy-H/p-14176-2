package com.back.domain.post.postComment.controller;

import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import com.back.domain.post.post.entitty.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.dto.PostCommentDto;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.service.PostCommentService;
import com.back.global.globalExceptionHandler.UnauthenticatedException;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name="ApiV1PostCommentController", description = "API 댓글 컨트롤러")
public class ApiV1PostCommentController {

    private final PostService postService;
    private final MemberService memberService;

    @GetMapping()
    @Operation(summary="다건 조회")
    public List<PostCommentDto> getItems(
            @PathVariable int postId) {
        Post post = postService.findById(postId).get();

        return post
                .getComments()
                .stream()
                .map(PostCommentDto::new)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary="단건 조회")
    public PostCommentDto getItem(
            @PathVariable int postId,
            @PathVariable int id
    ) {
        Post post = postService.findById(postId).get();

        PostComment postComment = post.findCommentById(id).get();

        return new PostCommentDto(postComment);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary="삭제")
    public RsData<Void> delete(
            @PathVariable int postId,
            @PathVariable int id,
            @RequestParam(defaultValue="0") int actorId
    ) {
        Member author = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
        Post post = postService.findById(postId).get();

        PostComment postComment = post.findCommentById(id).get();

        postService.deleteComment(author, post, postComment);

        return new RsData<Void>(
                "200-1",
                "%d번 댓글이 삭제되었습니다.".formatted(id));
    }

    public record PostCommentModifyReqBody (
            String comment
    ) {}

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary="수정")
    public RsData<PostCommentDto> modify(
            @PathVariable int postId,
            @PathVariable int id,
            @RequestParam(defaultValue="0") int actorId,
            @RequestBody @Validated PostCommentModifyReqBody req
    ) {
        Member author = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
        Post post = postService.findById(postId).get();
        PostComment comment = post.findCommentById(id).get();
        postService.modifyComment(author, comment, req.comment);

        return new RsData<>(
                "200-1",
                "%d번 댓글이 수정되었습니다.".formatted(id),
                new PostCommentDto(comment)
        );
    }

    public record PostCommentWriteReqBody(
            @NotBlank
            @Size(min = 2, max = 100)
            String content
    ) {
    }

    @PostMapping
    @Transactional
    @Operation(summary="작성")
    public RsData<PostCommentDto> write(
            @PathVariable int postId,
            @RequestParam(defaultValue="0") int actorId,
            @Valid @RequestBody PostCommentWriteReqBody reqBody
    ) {
        Member author = memberService.findById(actorId)
                .orElseThrow(UnauthenticatedException::new);
        Post post = postService.findById(postId).get();

        PostComment postComment = postService.writeComment(author, post, reqBody.content);

        postService.flush();

        return new RsData<>(
                "201-1",
                "%d번 댓글이 작성되었습니다.".formatted(postComment.getId()),
                new PostCommentDto(postComment)
        );
    }

}
