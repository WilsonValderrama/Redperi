import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PostItem, LikeResponse } from './posts.models';

@Injectable({ providedIn: 'root' })
export class PostsApi {
  private http = inject(HttpClient);

  getFeed(): Observable<PostItem[]> {
    return this.http.get<PostItem[]>('/api/posts');
  }

  createPost(mensaje: string): Observable<any> {
    return this.http.post('/api/posts', { mensaje });
  }

  toggleLike(postId: number): Observable<LikeResponse> {
    return this.http.post<LikeResponse>(`/api/posts/${postId}/like`, {});
  }
}