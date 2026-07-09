import { computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { signalStore, withState, withComputed, withMethods, patchState } from '@ngrx/signals';
import { LoginResponse } from './auth.models';

type AuthState = {
  token: string | null;
  username: string | null;
  alias: string | null;
  loading: boolean;
  error: string | null;
};

const initialState: AuthState = {
  token: localStorage.getItem('token'),
  username: localStorage.getItem('username'),
  alias: localStorage.getItem('alias'),
  loading: false,
  error: null,
};

export const AuthStore = signalStore(
  { providedIn: 'root' },              // <-- Singleton (requisito del enunciado)
  withState(initialState),
  withComputed((store) => ({
    isAuthenticated: computed(() => !!store.token()),
  })),
  withMethods((store, http = inject(HttpClient), router = inject(Router)) => ({
    login(username: string, password: string) {
      patchState(store, { loading: true, error: null });
      http.post<LoginResponse>('/auth/login', { username, password }).subscribe({
        next: (res) => {
          localStorage.setItem('token', res.token);
          localStorage.setItem('username', res.username);
          localStorage.setItem('alias', res.alias);
          patchState(store, {
            token: res.token,
            username: res.username,
            alias: res.alias,
            loading: false,
          });
          router.navigate(['/posts']);
        },
        error: () => {
          patchState(store, { loading: false, error: 'Usuario o clave incorrectos' });
        },
      });
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('alias');
      patchState(store, { token: null, username: null, alias: null });
      router.navigate(['/login']);
    },
  }))
);