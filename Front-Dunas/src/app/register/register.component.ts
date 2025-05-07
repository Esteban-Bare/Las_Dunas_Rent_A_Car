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
  constructor(public AuthService: AuthService,private router:Router) {}

  registerForm = new FormGroup({
    firstName: new FormControl<string>("",[
      Validators.required,
      Validators.minLength(2)
    ]),
    lastName: new FormControl<string>("",[
      Validators.required,
      Validators.minLength(2)
    ]),
    email: new FormControl<string>("",[
      Validators.required,
      Validators.email
    ]),
    password: new FormControl<string>("",[
      Validators.required,
      Validators.minLength(8)
    ]),
    confirmPassword: new FormControl<string>("",[
      Validators.required,
      Validators.minLength(8)
    ]),
    birthDate: new FormControl<string>("",[
      Validators.required,
      Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)
    ])
  })

  public async register(): Promise<void> {
    try {
      const firstName = this.registerForm.get('firstName')?.value!;
      const lastName = this.registerForm.get('lastName')?.value!;
      const email = this.registerForm.get('email')?.value!;
      const password = this.registerForm.get('password')?.value!;
      const confirmPassword = this.registerForm.get('confirmPassword')?.value!;
      const birthDate = this.registerForm.get('birthDate')?.value!;

      if (password !== confirmPassword) {
        console.error("Passwords do not match");
        return;
      }

      let registerData = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password,
        birthDate: birthDate
      }

      await this.AuthService.register(registerData);
      console.log("Registration successful");
      this.router.navigate(['/login']);
    } catch (error) {
      console.error("Registration failed:", error);
    }
  }
}
