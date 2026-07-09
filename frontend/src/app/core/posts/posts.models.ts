export interface PostItem {
  postId: number;
  mensaje: string;
  fechaPublicacion: string;
  autorId: number;
  autorAlias: string;
  totalLikes: number;
  likedByMe: boolean;
}

export interface CreatePostRequest {
  mensaje: string;
}

export interface LikeResponse {
  postId: number;
  totalLikes: number;
  likedByMe: boolean;
}