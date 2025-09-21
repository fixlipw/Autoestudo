package com.ufc.blog.repository;

import com.ufc.blog.entity.Post;
import com.ufc.blog.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    Page<Post> findByAuthorIdAndStatus(Long authorId, PostStatus status, Pageable pageable);

}