import { Injectable } from '@angular/core';
import {Credentials} from '../interface/credentials';
import { UserInfo } from '../interface/user-info';
import {BehaviorSubject, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly USER_INFO_KEY = 'user_info';
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);
  public currentUser$: Observable<UserInfo | null> = this.currentUserSubject.asObservable();

  public tempCreds: Credentials = {
    email : "admin@gmail.com",
    password : "123456"
  }

  constructor() {
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
    } catch (error) {
      console.error("Error during logout:", error);
    } finally {
      localStorage.removeItem(this.USER_INFO_KEY);
      this.currentUserSubject.next(null);
    }
  }

  public isLoggedIn(): boolean {
    return this.currentUserSubject.next !== null;
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
}
