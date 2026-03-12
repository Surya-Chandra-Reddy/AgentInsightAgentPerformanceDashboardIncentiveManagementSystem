import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    const isAuthRequest = req.url.includes('/users/register') || req.url.includes('/users/login');

    if (isAuthRequest) {
      return next.handle(req);
    }
    
    const token = this.authService.getToken();
    let authReq = req;
    
    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
    
    // Using .pipe() to intercept the response
    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        // Check if the error is 401 (Unauthorized)
        if (error.status === 401) {
          alert('Token expired or unauthorized. Logging out...');

          this.authService.clearToken();
          
          // 2. Redirect to login page
          this.router.navigate(['/login'], {
            queryParams: { expired: 'true' } // Optional: Show a message on login page
          });
        }
 
        // Return the error so the calling service can still handle it if needed
        return throwError(() => error);
      })
    );
  }

  private handleAuthError() {
    
    this.authService.logout(); 
    
    this.router.navigate(['/login']);
  }
}