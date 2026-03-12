import { Injectable, signal, inject } from '@angular/core';
import { Agent } from '../models/agent.model';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);

  isAuthenticated = signal(false);
  currentUser = signal<Agent | null>(null);

  private apiUrl = 'http://localhost:8080/users';
  private tokenKey = 'auth_token';

  constructor() {
    this.checkAuth();
  }

  async register(data: { fullName: string; email: string; password: string }): Promise<any> {
    const payload = {
      name: data.fullName,
      email: data.email,
      password: data.password
    };

    try {
      const response = await firstValueFrom(
        this.http.post(`${this.apiUrl}/register`, payload)
      );
      return response;
    } catch (err) {
      // DO NOT just return false here.
      // Throw the error so the component knows WHY it failed.
      throw err;
    }
  }

  async login(email: string, password: string): Promise<boolean> {
    try {
      const payload = { email, password };
      const response = await firstValueFrom(
        this.http.post<any>(
          `${this.apiUrl}/login`,
          payload
        )
      );

      console.log('Login response:', response);
      console.log('Name:', response.agentname);

      // Store token
      localStorage.setItem(this.tokenKey, response.token);

      // Set user data
      const user: Agent = {
        agentid:response.agentid || '',
        name: response.agentname || '',
        email,
        phone: response.phoneno || '',
        role: (response.role ?? '').toLowerCase() as 'admin' | 'agent'
      };

      this.currentUser.set(user);
      this.isAuthenticated.set(true);
      localStorage.setItem('user', JSON.stringify(user));

      return true;
    } catch (err) {
      console.error('Login error:', err);
      return false;
    }
  }

  getToken(): string | null {
    // use tokenKey consistently (was hard-coded before)
    return localStorage.getItem(this.tokenKey);
  }

  logout(): void {
    this.clearToken(); // You can now use it here for better code reuse
    localStorage.removeItem('user');
    this.isAuthenticated.set(false);
    this.currentUser.set(null);
  }

  private checkAuth(): void {
    try {
      const token = this.getToken();
      const userJson = localStorage.getItem('user');

      if (token && userJson) {
        const user = JSON.parse(userJson) as Agent;
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
      }
    } catch (err) {
      console.warn('Auth check failed:', err);
      this.logout();
    }
  }

  setCurrentUser(user: Agent | null): void {
    this.currentUser.set(user);
    this.isAuthenticated.set(!!user);

    if (user) {
      localStorage.setItem('user', JSON.stringify(user));
    } else {
      localStorage.removeItem('user');
    }
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
  }
}
