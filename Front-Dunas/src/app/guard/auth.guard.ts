import { Injectable } from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import { AuthService } from '../service/auth.service';
import { AuthorizationService} from '../service/authorization.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router, private authorizationService: AuthorizationService) {}

  async canActivate(route:ActivatedRouteSnapshot,state: RouterStateSnapshot): Promise<boolean> {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }
    console.log("Pass 1° if");

    const isValid = await this.authService.checkAuthStatus();
    if (!isValid) {
      this.router.navigate(['/login']);
      return false;
    }
    console.log("Pass 2° if");

    if (route.data['resource']) {
      const hasAccess = await this.authorizationService.canAccess(route.data['resource']);
      if (!hasAccess) {
        this.router.navigate(['/unauthorized']);
        return false;
      }
    }
    console.log("Pass 3° if");

    return true;
  }
}
