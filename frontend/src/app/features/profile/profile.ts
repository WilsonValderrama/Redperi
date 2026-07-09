import { Component, inject, signal, OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { RouterLink } from '@angular/router';
import { ProfileApi } from '../../core/auth/profile-api';
import { ProfileData } from '../../core/auth/profile.models';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [DatePipe, MatCardModule, MatButtonModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile implements OnInit {
  private api = inject(ProfileApi);
  profile = signal<ProfileData | null>(null);

  ngOnInit() {
    this.api.getProfile().subscribe((p) => this.profile.set(p));
  }
}
