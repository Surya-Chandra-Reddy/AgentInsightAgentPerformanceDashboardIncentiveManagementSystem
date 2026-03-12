import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { SalesService } from '../../../services/sales.service';
import { IncentivesService } from '../../../services/incentives.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './admin-dashboard.html',
  styleUrls: ['./admin-dashboard.css'],
})
export class AdminDashboard implements OnInit { //OnInit is a interface here s if we use this we need to have a ngOnInit method in the body of the class
  // Key metrics
  totalSales: number = 0;
  totalAgents: number = 0;
  totalPolicies: number = 0;
  incentivesPaid: number = 0;
  pendingApprovals: number = 0;
  completedSales: number = 0;
  cancelledSales: number = 0;

  // Data arrays
  recentSales: any[] = [];
  topAgents: any[] = [];
  salesByStatus: any[] = [];
  monthlySales: any[] = [];
  maxMonthlyAmount: number = 1;

  constructor(
    private salesService: SalesService,
    private incentivesService: IncentivesService
  ) {}

  async ngOnInit(): Promise<void> {
    await this.loadDashboardData();
  }

  async loadDashboardData(): Promise<void> {
    try {
      // Load all sales with details
      const allSales = await this.salesService.getAllSalesWithDetails();
      const agents = await this.salesService.getAgents();
      const policies = await this.salesService.getPolicies();

      // Basic metrics
      this.totalSales = await this.salesService.getTotalSalesAmount();
      this.totalAgents = agents.length;
      this.totalPolicies = policies.length;
      this.incentivesPaid = await this.incentivesService.getTotalIncentivesAmount();

      // Sales status breakdown
      this.completedSales = allSales.filter(s => s.status === 'Completed').length;
      this.pendingApprovals = allSales.filter(s => s.status === 'Pending').length;
      this.cancelledSales = allSales.filter(s => s.status === 'Cancelled').length;

      // Recent sales (last 5)
      this.recentSales = allSales
        .sort((a, b) => new Date(b.saleDate).getTime() - new Date(a.saleDate).getTime())
        .slice(0, 5);

      // Top performing agents by sales amount
      const agentSales = agents.map(agent => {
        const agentSalesData = allSales.filter(s => s.agentid === agent.agentid);
        const totalAmount = agentSalesData.reduce((sum, s) => sum + s.amount, 0);
        const salesCount = agentSalesData.length;
        return {
          name: agent.name,
          totalAmount,
          salesCount
        };
      }).sort((a, b) => b.totalAmount - a.totalAmount).slice(0, 5);

      this.topAgents = agentSales;

      // Sales by status for chart
      this.salesByStatus = [
        { status: 'Completed', count: this.completedSales, color: '#10b981' },
        { status: 'Pending', count: this.pendingApprovals, color: '#f59e0b' },
        { status: 'Cancelled', count: this.cancelledSales, color: '#ef4444' }
      ];

      // Monthly sales trend (mock data based on available dates)
      this.monthlySales = this.calculateMonthlySales(allSales);
      this.maxMonthlyAmount = Math.max(...this.monthlySales.map(m => m.amount), 1);

    } catch (error) {
      console.error('Error loading dashboard data:', error);
    }
  }

  private calculateMonthlySales(sales: any[]): any[] {
    const monthlyData: { [key: string]: number } = {};

    sales.forEach(sale => {
      if (sale.saleDate) {
        const date = new Date(sale.saleDate);
        const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
        monthlyData[monthKey] = (monthlyData[monthKey] || 0) + sale.amount;
      }
    });

    return Object.entries(monthlyData)
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([month, amount]) => ({ month, amount }));
  }
}
