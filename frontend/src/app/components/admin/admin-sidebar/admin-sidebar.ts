import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLinkWithHref, Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';


@Component({
  selector: 'app-admin-sidebar',
  imports: [RouterOutlet, RouterLinkWithHref],
  templateUrl: './admin-sidebar.html',
  styleUrl: './admin-sidebar.css',
})
export class AdminSidebar {
  isLoggingOut = signal(false);

  constructor(private authService: AuthService, private router: Router) {}

  logout(): void { //
    this.isLoggingOut.set(true);
    setTimeout(() => {
      this.authService.logout();
      this.router.navigate(['/login']);
    }, 1000);
  }
}
