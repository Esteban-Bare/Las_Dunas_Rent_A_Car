import { CanActivateFn } from '@angular/router';
import { AuthService } from '../service/auth.service';
import {inject} from '@angular/core';
import { Router } from '@angular/router';

export const LoggedGuard = async () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isValid = await authService.checkAuthStatus();
  if (isValid) {
    router.navigate(['/dashboard']).then(r => {
      if (r) {
        console.log("Redirection to dashboard successful");
      } else {
        console.log("Redirection to dashboard failed");
      }
    });
    return false;
  }


  if (authService.isLoggedIn()) {
    router.navigate(['/dashboard']).then(r => {
      if (r) {
        console.log("Redirection to dashboard successful");
      } else {
        console.log("Redirection to dashboard failed");
      }
    });
    return false;
  }
  return true;
};
