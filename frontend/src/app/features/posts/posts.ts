import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { AuthStore } from '../../core/auth/auth-store';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-posts',
  standalone: true,
  imports: [MatButtonModule, RouterLink],
  templateUrl: './posts.html',
  styleUrl: './posts.scss',
})
export class Posts {
  store = inject(AuthStore);
}
