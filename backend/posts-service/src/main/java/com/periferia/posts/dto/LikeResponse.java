package com.periferia.posts.dto;

public record LikeResponse(Long postId, Long totalLikes, Boolean likedByMe) {}
