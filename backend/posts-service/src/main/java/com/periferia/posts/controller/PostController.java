package com.periferia.posts.controller;

import com.periferia.posts.dto.CreatePostRequest;
import com.periferia.posts.dto.FeedItemResponse;
import com.periferia.posts.dto.LikeResponse;
import com.periferia.posts.security.AuthUtils;
import com.periferia.posts.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@Tag(name = "Publicaciones", description = "Feed, creación de publicaciones y likes")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "Listar publicaciones", description = "Devuelve el feed con total de likes y si el usuario ya dio like")
    @GetMapping
    public ResponseEntity<List<FeedItemResponse>> getFeed(Authentication auth) {
        Long userId = AuthUtils.getUserId(auth);
        return ResponseEntity.ok(postService.getFeed(userId));
    }

    @Operation(summary = "Crear publicación", description = "Crea una publicación para el usuario autenticado")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication auth) {
        Long userId = AuthUtils.getUserId(auth);
        Long newId = postService.createPost(userId, request.mensaje());
        return ResponseEntity.ok(Map.of("id", newId, "mensaje", request.mensaje()));
    }

    @Operation(summary = "Dar/quitar like", description = "Alterna el like en una publicación y notifica por WebSocket")
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeResponse> like(@PathVariable Long id, Authentication auth) {
        Long userId = AuthUtils.getUserId(auth);
        return ResponseEntity.ok(postService.toggleLike(userId, id));
    }
}