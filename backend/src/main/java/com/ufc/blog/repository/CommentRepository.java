package com.ufc.blog.repository;

import com.ufc.blog.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> findByPostId(Long postId, Pageable pageable);
    Page<Comment> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.author.id = :authorId AND c.post.status = 'PUBLISHED'")
    Page<Comment> findPublishedCommentsByAuthorId(Long authorId, Pageable pageable);

}