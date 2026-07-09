import { Component, inject, signal, OnInit, OnDestroy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatToolbarModule } from '@angular/material/toolbar';
import { RouterLink } from '@angular/router';
import { AuthStore } from '../../core/auth/auth-store';
import { PostsStore } from '../../core/posts/posts-store';
import { WebSocketService } from '../../core/posts/websocket-service';

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [
    DatePipe, FormsModule, MatCardModule, MatButtonModule, MatIconModule,
    MatFormFieldModule, MatInputModule, MatToolbarModule, RouterLink,
  ],
  templateUrl: './posts.html',
  styleUrl: './posts.scss',
})
export class Posts implements OnInit, OnDestroy {
  authStore = inject(AuthStore);
  postsStore = inject(PostsStore);
  private ws = inject(WebSocketService);
  nuevoMensaje = signal('');

  ngOnInit() {
    this.postsStore.loadFeed();
    // conectar al canal de likes en tiempo real
    this.ws.connect((postId, totalLikes) => {
      this.postsStore.updateLikesFromSocket(postId, totalLikes);
    });
  }

  ngOnDestroy() {
    this.ws.disconnect();
  }

  publicar() {
    const msg = this.nuevoMensaje().trim();
    if (msg) {
      this.postsStore.createPost(msg);
      this.nuevoMensaje.set('');
    }
  }

  darLike(postId: number) {
    this.postsStore.toggleLike(postId);
  }
}