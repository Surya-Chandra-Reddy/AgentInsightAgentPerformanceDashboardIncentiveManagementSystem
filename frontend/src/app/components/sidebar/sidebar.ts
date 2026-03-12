import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {

  agent_title = 'AgentInsight';
  admin_title = 'AdminInsight';

  menuItems = [
    { icon: '📊', label: 'Dashboard', route: '/agent/dashboard' },
    { icon: '💼', label: 'Sales', route: '/agent/sales' },
    { icon: '🏆', label: 'Incentives', route: '/agent/incentives' },
    { icon: '📈', label: 'Leaderboard', route: '/agent/leaderboard' },
    { icon: '👤', label: 'Profile', route: '/agent/profile' }
  ];

  constructor(private authService: AuthService, private router: Router) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
