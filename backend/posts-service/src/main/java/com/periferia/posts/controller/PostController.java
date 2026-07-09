package com.periferia.posts.controller;

import com.periferia.posts.dto.CreatePostRequest;
import com.periferia.posts.dto.FeedItemResponse;
import com.periferia.posts.dto.LikeResponse;
import com.periferia.posts.security.AuthUtils;
import com.periferia.posts.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<FeedItemResponse>> getFeed(Authentication auth) {
        Long userId = AuthUtils.getUserId(auth);
        return ResponseEntity.ok(postService.getFeed(userId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication auth) {
        Long userId = AuthUtils.getUserId(auth);
        Long newId = postService.createPost(userId, request.mensaje());
        return ResponseEntity.ok(Map.of("id", newId, "mensaje", request.mensaje()));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> like(@PathVariable Long id, Authentication auth) {
        Long userId = AuthUtils.getUserId(auth);
        return ResponseEntity.ok(postService.toggleLike(userId, id));
    }
}
