package com.back.domain.post.post.service;

import com.back.domain.member.member.entity.Member;
import com.back.domain.post.post.entitty.Post;
import com.back.domain.post.post.repository.PostRepository;
import com.back.domain.post.postComment.entity.PostComment;
import com.back.global.globalExceptionHandler.AccessDeniedException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public long count() {
        return postRepository.count();
    }

    public Post write(Member author, String title, String content) {
        return postRepository.save(new Post(author, title, content));
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    public boolean deleteComment(Member author, Post post, PostComment postComment) {
        if (!author.equals(postComment.getAuthor())) {
            throw new AccessDeniedException();
        }
        return post.deleteComment(postComment);
    }

    public void delete(Member author, Post post) {
        if (!author.equals(post.getAuthor())) {
            throw new AccessDeniedException();
        }
        postRepository.delete(post);
    }

    public Optional<Post> findLatest() {
        return postRepository.findFirstByOrderByIdDesc();
    }

    public PostComment writeComment(Member author, Post post, String content) {

        return post.addComment(author, content);
    }
    
    public void flush() {
        postRepository.flush();
    }

    public void modify(Member author, Post post, String title, String content) {
        if (!author.equals(post.getAuthor())) {
            throw new AccessDeniedException();
        }
        post.modify(title, content);
    }

    public void modifyComment(Member author, PostComment comment, String content) {
        if (!author.equals(comment.getAuthor())) {
            throw new AccessDeniedException();
        }
        comment.modify(content);
    }
}
