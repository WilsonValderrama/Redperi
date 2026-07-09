package com.periferia.posts.service;

import com.periferia.posts.dto.FeedItemResponse;
import com.periferia.posts.dto.LikeResponse;
import com.periferia.posts.repository.PostRepository;
import com.periferia.posts.repository.ProcedureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ProcedureRepository procedureRepository;

    public PostService(PostRepository postRepository,
                       ProcedureRepository procedureRepository) {
        this.postRepository = postRepository;
        this.procedureRepository = procedureRepository;
    }

    public List<FeedItemResponse> getFeed(Long currentUserId) {
        return postRepository.getFeed(currentUserId).stream()
                .map(r -> new FeedItemResponse(
                        r.getPost_id(),
                        r.getMensaje(),
                        r.getFecha_publicacion(),
                        r.getAutor_id(),
                        r.getAutor_alias(),
                        r.getTotal_likes(),
                        r.getLiked_by_me()
                ))
                .toList();
    }

    public Long createPost(Long userId, String mensaje) {
        return procedureRepository.createPost(userId, mensaje);
    }

    public LikeResponse toggleLike(Long userId, Long postId) {
        Long total = procedureRepository.toggleLike(userId, postId);
        return new LikeResponse(postId, total);
    }
}
