package com.back.domain.post.post.repository;

import com.back.domain.post.post.entitty.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    Optional<Post> findFirstByOrderByIdDesc();
}
