import { Injectable } from '@angular/core';
import {Router} from '@angular/router';
import { AuthService} from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthorizationService {

  constructor(private router:Router, private authService: AuthService) {}

  public async canAccess(resource: string): Promise<boolean> {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    try {
      const response = await fetch(`http://localhost:8077/auth/can-access/${resource}`, {
        credentials: 'include'
      });

      if (response.ok) {
        const data = await response.json();
        return data.authorized === true;
      }

      return false;
    } catch (e) {
      console.error("Error checking access:", e);
      return false;
    }
  }
}
