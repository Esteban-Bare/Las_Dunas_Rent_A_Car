import { Component } from '@angular/core';
import { AuthService } from '../service/auth.service';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { Credentials } from '../interface/credentials'
import {Router, RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  loginError: string = '';
  isLoading: boolean = false;

  constructor(public AuthService: AuthService, private router: Router) {}

  loginForm = new FormGroup({
    email: new FormControl<string>("", [
      Validators.required,
      Validators.email
    ]),
    password: new FormControl<string>("", [
      Validators.required,
      Validators.minLength(6)
    ])
  })

  public async login(): Promise<void> {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.loginError = '';

    try {
      const email = this.loginForm.get('email')?.value!;
      const password = this.loginForm.get('password')?.value!;
      let credentials: Credentials = {
        email: email,
        password: password
      }
      await this.AuthService.login(credentials);
      console.log("Login successful");
      this.router.navigate(['/dashboard']);
    } catch (error) {
      console.error("Login failed:", error);
      this.loginError = 'Invalid email or password. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }
}
