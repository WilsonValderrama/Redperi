import { inject } from '@angular/core';
import { signalStore, withState, withMethods, patchState } from '@ngrx/signals';
import { PostsApi } from './posts-api';
import { PostItem } from './posts.models';

type PostsState = {
  posts: PostItem[];
  loading: boolean;
  error: string | null;
};

const initialState: PostsState = {
  posts: [],
  loading: false,
  error: null,
};

export const PostsStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store, api = inject(PostsApi)) => ({
    loadFeed() {
      patchState(store, { loading: true, error: null });
      api.getFeed().subscribe({
        next: (posts) => patchState(store, { posts, loading: false }),
        error: () => patchState(store, { loading: false, error: 'Error al cargar publicaciones' }),
      });
    },
    createPost(mensaje: string) {
      api.createPost(mensaje).subscribe({
        next: () => this.loadFeed(),   // recarga el feed tras crear
      });
    },
    toggleLike(postId: number) {
      api.toggleLike(postId).subscribe({
        next: (res) => {
          // actualiza solo ese post en la lista, sin recargar todo
          const updated = store.posts().map((p) =>
            p.postId === res.postId
              ? { ...p, totalLikes: res.totalLikes, likedByMe: res.likedByMe }
              : p
          );
          patchState(store, { posts: updated });
        },
      });
    },
    // método para actualizar likes desde el WebSocket
    updateLikesFromSocket(postId: number, totalLikes: number) {
      const updated = store.posts().map((p) =>
        p.postId === postId ? { ...p, totalLikes } : p
      );
      patchState(store, { posts: updated });
    },
  }))
);