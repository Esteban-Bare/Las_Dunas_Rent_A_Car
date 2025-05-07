import { Injectable } from '@angular/core';
import {Credentials} from '../interface/credentials';
import { UserInfo } from '../interface/user-info';
import {RegisterData} from '../interface/register-data';
import {BehaviorSubject, Observable} from 'rxjs';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly USER_INFO_KEY = 'user_info';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);
  public currentUser$: Observable<UserInfo | null> = this.currentUserSubject.asObservable();

  constructor(private router: Router) {
    const savedUserInfo = localStorage.getItem(this.USER_INFO_KEY);
    if (savedUserInfo) {
      this.currentUserSubject.next(JSON.parse(savedUserInfo));
    }
  }

  public async login(creds: Credentials): Promise<UserInfo> {
    try {
      const res = await fetch("http://localhost:8077/auth/login", {
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(creds),
        credentials: 'include'
      });

      if (!res.ok) {
        throw new Error("Login failed");
      }

      const data = await res.json();

      const userInfo: UserInfo = {
        email: data.sub,
        role: data.role,
        token: ""
      }

      this.setUserInfo(userInfo);
      return userInfo;
    } catch (error) {
      console.error("Error during login:", error);
      throw error;
    }
  }

  public async logout(): Promise<void> {
    try {
      await fetch("http://localhost:8077/auth/logout", {
        method: "POST",
        credentials: 'include'
      });
      console.log("Logout successful");
      this.router.navigate(['/']);
    } catch (error) {
      console.error("Error during logout:", error);
    } finally {
      localStorage.removeItem(this.USER_INFO_KEY);
      this.currentUserSubject.next(null);
    }
  }

  public isLoggedIn(): boolean {
    return this.currentUserSubject.value !== null;
  }

  public async checkAuthStatus(): Promise<boolean> {
    console.log("Checking auth status...");
    try {
      if (!this.currentUserSubject.value) {
        return false;
      }

      const res = await fetch("http://localhost:8077/auth/validate",
        {
          method: "POST",
          credentials: "include"
        });

      if (!res.ok) {
        this.logout();
        return false;
      }

      return true
    } catch (e) {
      console.error("Error during auth status check:", e);
      return false;
    }
  }

  private setUserInfo(userInfo: UserInfo): void {
    localStorage.setItem(this.USER_INFO_KEY, JSON.stringify(userInfo));
    this.currentUserSubject.next(userInfo);
  }

  public getUserInfo(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  public getRole(): string | null {
    return this.currentUserSubject.value?.role || null;
  }

  public getEmail(): string | null {
    return this.currentUserSubject.value?.email || null;
  }

  async register(registerData: RegisterData) {
    try {
      const res = await fetch("http://localhost:8077/auth/register", {
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(registerData),
        credentials: 'include'
      });
      console.log(res);

      if (!res.ok) {
        throw new Error("Registration failed");
      }

      const data = await res.json();
      console.log(data);
    } catch (error) {
      console.error("Error during registration:", error);
      throw error;
    }
  }
}
