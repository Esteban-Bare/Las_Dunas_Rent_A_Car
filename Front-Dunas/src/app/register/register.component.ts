import { Component } from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {AuthService} from '../service/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, NgIf],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  registerError: string = '';
  isLoading: boolean = false;

  constructor(public AuthService: AuthService, private router: Router) {}

  registerForm = new FormGroup({
    firstName: new FormControl<string>("", [
      Validators.required,
      Validators.minLength(2)
    ]),
    lastName: new FormControl<string>("", [
      Validators.required,
      Validators.minLength(2)
    ]),
    email: new FormControl<string>("", [
      Validators.required,
      Validators.email
    ]),
    password: new FormControl<string>("", [
      Validators.required,
      Validators.minLength(8)
    ]),
    confirmPassword: new FormControl<string>("", [
      Validators.required,
      Validators.minLength(8)
    ]),
    birthDate: new FormControl<string>("", [
      Validators.required,
      Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)
    ])
  })

  public async register(): Promise<void> {
    if (this.registerForm.invalid || !this.passwordsMatch()) {
      return;
    }

    this.isLoading = true;
    this.registerError = '';

    try {
      const firstName = this.registerForm.get('firstName')?.value!;
      const lastName = this.registerForm.get('lastName')?.value!;
      const email = this.registerForm.get('email')?.value!;
      const password = this.registerForm.get('password')?.value!;
      const birthDate = this.registerForm.get('birthDate')?.value!;

      let registerData = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password,
        birthDate: birthDate
      }

      console.log("Registering user:", registerData);
      await this.AuthService.register(registerData);
      console.log("Registration successful");
      this.router.navigate(['/login']);
    } catch (error) {
      console.error("Registration failed:", error);
      this.registerError = 'Registration failed. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }

  passwordsMatch(): boolean {
    const password = this.registerForm.get('password')?.value;
    const confirmPassword = this.registerForm.get('confirmPassword')?.value;
    return password === confirmPassword;
  }

  getPasswordLength(): number {
    return this.registerForm.get('password')?.value?.length || 0;
  }

  hasUppercase(): boolean {
    const password = this.registerForm.get('password')?.value || '';
    return /[A-Z]/.test(password);
  }

  hasLowercase(): boolean {
    const password = this.registerForm.get('password')?.value || '';
    return /[a-z]/.test(password);
  }

  hasNumber(): boolean {
    const password = this.registerForm.get('password')?.value || '';
    return /\d/.test(password);
  }
}
