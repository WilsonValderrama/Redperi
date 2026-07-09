package com.periferia.posts.dto;

import java.time.LocalDateTime;

public record FeedItemResponse(
        Long postId,
        String mensaje,
        LocalDateTime fechaPublicacion,
        Long autorId,
        String autorAlias,
        Long totalLikes,
        Boolean likedByMe
) {}
