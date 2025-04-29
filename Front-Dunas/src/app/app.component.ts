import { Component } from '@angular/core';
import {RouterLink, RouterModule, RouterOutlet} from '@angular/router';
import { CommonModule } from '@angular/common';
import {AuthService} from './service/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,RouterModule, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'Front-Dunas';
  constructor(protected authService: AuthService) {}
}
