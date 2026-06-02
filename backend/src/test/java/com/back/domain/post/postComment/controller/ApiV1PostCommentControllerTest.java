package com.back.domain.post.postComment.controller;

import com.back.domain.post.post.controller.ApiV1PostController;
import com.back.domain.post.post.entitty.Post;
import com.back.domain.post.post.service.PostService;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.domain.post.postComment.service.PostCommentService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApiV1PostCommentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostCommentService postCommentService;
    @Autowired
    private PostService postService;

    @Test
    @DisplayName("댓글 단건조회")
    void t1() throws Exception {
        int postId = 1;
        int commentId = 1;
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/%d/comments/%d".formatted(postId, commentId))
                ).andDo(print());

        PostComment comment = postService.findById(postId).get()
                .findCommentById(commentId).get();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createDate").isString())
                .andExpect(jsonPath("$.modifyDate").isString())
                .andExpect(jsonPath("$.content").isString());

    }

    @Test
    @DisplayName("댓글 다건조회")
    void t2() throws Exception {
        int postId = 1;
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/%d/comments".formatted(postId))
                )
                .andDo(print());

        List<PostComment> comments = postService.findById(postId).get().getComments();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(status().isOk());

        for (int i = 0; i < comments.size(); i++) {
            PostComment comment = comments.get(i);
            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(comment.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(Matchers.startsWith(comment.getCreateDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(Matchers.startsWith(comment.getModifyDate().toString().substring(0, 20))))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(comment.getContent()));
        }
    }

    @Test
    @DisplayName("댓글 삭제")
    void t3() throws Exception {
        int postId = 1;
        int id = 1;

        ResultActions resultActions = mvc
                .perform(delete("/api/v1/posts/%d/comments/%d?actorId=%d".formatted(postId, id, 3)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 삭제되었습니다.".formatted(id)));
    }
    @Test
    @DisplayName("글 삭제 with wrong actorId")
    void t3_1() throws Exception {
        int postId = 1;
        int id = 1;

        ResultActions resultActions = mvc
                .perform(delete("/api/v1/posts/%d/comments/%d?actorId=%d".formatted(id, postId, 1)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.resultCode").value("403-1"))
                .andExpect(jsonPath("$.msg").value("권한이 없습니다"));
    }

    @Test
    @DisplayName("글 삭제 without actorId")
    void t3_2() throws Exception {
        int id = 1;
        int postId = 1;

        ResultActions resultActions = mvc
                .perform(delete("/api/v1/posts/%d/comments/%d".formatted(id, postId)))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.resultCode").value("401-1"))
                .andExpect(jsonPath("$.msg").value("로그인 후 이용해주세요."));
    }

    @Test
    @DisplayName("댓글 수정")
    void t4() throws Exception {

        int postId = 1;
        int id = 1;


        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/%d/comments/%d?actorId=%d".formatted(postId, id, 3))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "내용 수정"
                                        }
                                        """)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 수정되었습니다.".formatted(id)));
    }

    @Test
    @DisplayName("댓글 작성")
    void t5() throws Exception {
        int postId = 1;

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts/%d/comments?actorId=%d".formatted(postId, 3))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "content": "내용"
                                        }
                                        """)
                )
                .andDo(print());

        Post post = postService.findById(postId).get();

        PostComment postComment = post.getComments().getLast();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostCommentController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.msg").value("%d번 댓글이 작성되었습니다.".formatted(postComment.getId())))
                .andExpect(jsonPath("$.data.id").value(postComment.getId()))
                .andExpect(jsonPath("$.data.createDate").value(Matchers.startsWith(postComment.getCreateDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.modifyDate").value(Matchers.startsWith(postComment.getModifyDate().toString().substring(0, 20))))
                .andExpect(jsonPath("$.data.content").value("내용"));
    }
}
