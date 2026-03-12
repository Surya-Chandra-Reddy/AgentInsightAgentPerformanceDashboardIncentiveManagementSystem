import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    // ✅ Use the signal directly
    if (this.authService.isAuthenticated()) {
      return true;
    }

    // ✅ If not authenticated, redirect to login
    this.router.navigate(['/login']);
    return false;
  }
}
