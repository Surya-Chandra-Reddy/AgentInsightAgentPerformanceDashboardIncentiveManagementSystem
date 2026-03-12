import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { AgentDashboard } from './components/agent-dashboard/agent-dashboard';
import { AgentSales } from './components/agent/agent-sales/agent-sales';
import { AgentIncentives } from './components/agent/agent-incentives/agent-incentives';
import { AgentLeaderboard } from './components/agent/agent-leaderboard/agent-leaderboard';
import { AgentProfile } from './components/agent/agent-profile/agent-profile';
import { AuthGuard } from './guards/auth.guard';
import { AdminSidebar } from './components/admin/admin-sidebar/admin-sidebar';
import { AdminDashboard } from './components/admin/admin-dashboard/admin-dashboard';
import { AgentManagement } from './components/admin/agent-management/agent-management';
import { AdminSales } from './components/admin/sales/admin-sales';
import { AdminIncentives } from './components/admin/admin-incentives/admin-incentives';
import { Performance } from './components/admin/performance/performance';
import { Reports } from './components/admin/reports/reports';
import { App } from './app';
import { Home } from './components/home/home';
import { Register } from './components/register/register';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'home', component: Home },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  {
    path: 'admin',
    component: AdminSidebar,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: AdminDashboard },
      { path: 'agent-management', component: AgentManagement },
      { path: 'sales', component: AdminSales },
      { path: 'incentives', component: AdminIncentives },
      { path: 'reports', component: Reports },
      { path: 'performance', component: Performance },
    ]
  },
  {
    path: 'agent',
    canActivate: [AuthGuard],
    children: [
      { path: 'dashboard', component: AgentDashboard },
      { path: 'sales', component: AgentSales },
      { path: 'incentives', component: AgentIncentives },
      { path: 'leaderboard', component: AgentLeaderboard },
      { path: 'profile', component: AgentProfile },
      { path:'register', component: Register},
      { path: 'register', component: Register }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
