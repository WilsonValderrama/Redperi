import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProfileData } from './profile.models';

@Injectable({ providedIn: 'root' })
export class ProfileApi {
  private http = inject(HttpClient);

  getProfile(): Observable<ProfileData> {
    return this.http.get<ProfileData>('/api/auth/profile');
  }
}