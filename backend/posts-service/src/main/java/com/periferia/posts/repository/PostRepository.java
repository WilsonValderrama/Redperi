package com.periferia.posts.repository;

import com.periferia.posts.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * FROM fn_get_feed(:currentUserId)", nativeQuery = true)
    List<FeedRow> getFeed(@Param("currentUserId") Long currentUserId);

    interface FeedRow {
        Long getPost_id();
        String getMensaje();
        LocalDateTime getFecha_publicacion();
        Long getAutor_id();
        String getAutor_alias();
        Long getTotal_likes();
        Boolean getLiked_by_me();
    }
}
