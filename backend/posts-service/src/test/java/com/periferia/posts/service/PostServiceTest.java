package com.periferia.posts.service;

import com.periferia.posts.dto.LikeResponse;
import com.periferia.posts.repository.PostRepository;
import com.periferia.posts.repository.ProcedureRepository;
import com.periferia.posts.repository.ProcedureRepository.LikeResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ProcedureRepository procedureRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private PostService postService;

    @Test
    void toggleLike_devuelveTotalYNotificaPorWebSocket() {
        // arrange: el procedimiento devuelve 5 likes y likedByMe = true
        when(procedureRepository.toggleLike(1L, 2L))
                .thenReturn(new LikeResult(5L, true));

        // act
        LikeResponse response = postService.toggleLike(1L, 2L);

        // assert: el resultado es correcto
        assertThat(response.postId()).isEqualTo(2L);
        assertThat(response.totalLikes()).isEqualTo(5L);
        assertThat(response.likedByMe()).isTrue();

        // assert: se publicó el mensaje por WebSocket al topic correcto
        verify(messagingTemplate).convertAndSend(eq("/topic/likes"), any(LikeResponse.class));
    }

    @Test
    void createPost_delegaEnElProcedimiento() {
        when(procedureRepository.createPost(1L, "Hola mundo")).thenReturn(10L);

        Long newId = postService.createPost(1L, "Hola mundo");

        assertThat(newId).isEqualTo(10L);
        // verifica que se llamó al procedimiento con los datos correctos
        verify(procedureRepository).createPost(1L, "Hola mundo");
    }

    @Test
    void getFeed_mapeaLasFilasADto() {
        // crea una fila simulada de la proyección
        var row = mock(PostRepository.FeedRow.class);
        when(row.getPost_id()).thenReturn(1L);
        when(row.getMensaje()).thenReturn("Publicación de prueba");
        when(row.getAutor_alias()).thenReturn("rosa");
        when(row.getTotal_likes()).thenReturn(3L);
        when(row.getLiked_by_me()).thenReturn(true);

        when(postRepository.getFeed(1L)).thenReturn(java.util.List.of(row));

        var feed = postService.getFeed(1L);

        assertThat(feed).hasSize(1);
        assertThat(feed.get(0).mensaje()).isEqualTo("Publicación de prueba");
        assertThat(feed.get(0).autorAlias()).isEqualTo("rosa");
        assertThat(feed.get(0).totalLikes()).isEqualTo(3L);
        assertThat(feed.get(0).likedByMe()).isTrue();
    }

}
