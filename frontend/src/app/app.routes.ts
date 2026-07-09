import { Routes } from '@angular/router';
import { Login } from './features/login/login';
import { Posts } from './features/posts/posts';
import { authGuard } from './core/auth/auth-guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'posts', component: Posts, canActivate: [authGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];
