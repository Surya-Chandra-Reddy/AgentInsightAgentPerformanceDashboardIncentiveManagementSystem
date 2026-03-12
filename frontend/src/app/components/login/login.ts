import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink,FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  // Signals to hold form values and UI states
  email = signal<string>('');
  password = signal<string>('');
  error = signal<string>('');
  loading = signal<boolean>(false);

  constructor(private authService: AuthService, private router: Router) {}

  /**
   * Handles form submission for login.
   * - Prevents default form submission
   * - Validates presence of email/password (template handles format/length)
   * - Calls AuthService.login and routes based on role
   * - Sets meaningful error messages on failure
   */
  async onSubmit(event: Event): Promise<void> {
    event.preventDefault();

    // Clear previous error
    this.error.set('');

    // Client-side guard (template already enforces required/invalid state)
    const emailVal = this.email().trim();
    const passwordVal = this.password();

    if (!emailVal || !passwordVal) {
      this.error.set('Please fill in all fields');
      return;
    }

    this.loading.set(true);

    try {
      const success = await this.authService.login(emailVal, passwordVal);

      if (success) {
        const currentUser = this.authService.currentUser();

        // Route based on role; only 'agent' goes to agent dashboard
        if (currentUser?.role === 'admin') {
          await this.router.navigate(['/admin']);
        } else if (currentUser?.role === 'agent') {
          await this.router.navigate(['/agent/dashboard']);
        } else {
          this.error.set('Invalid role');
          await this.router.navigate(['/login']);
        }
      } else {
        // When login returns false (e.g., invalid credentials)
        this.error.set('Invalid email or password');
      }
    } catch (err: unknown) {
      // Network/server errors
      console.error('Login error:', err);
      this.error.set('Login failed. Please try again.');
    } finally {
      this.loading.set(false);
    }
   }
  
}