import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Sidebar } from '../sidebar/sidebar';
import { RouterLink, Router } from '@angular/router';
import { SalesService } from '../../services/sales.service';
import { IncentivesService } from '../../services/incentives.service';
import { LeaderboardService } from '../../services/leaderboard.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-agent-dashboard',
  standalone: true,
  imports: [CommonModule, Sidebar, RouterLink],
  templateUrl: './agent-dashboard.html',
  styleUrl: './agent-dashboard.css'
})
export class AgentDashboard implements OnInit {
  stats = [
    { label: 'Total Sales', value: '₹0', icon: '📈' },
    { label: 'Incentives Earned', value: '₹0', icon: '🏆' },
    { label: 'Rank', value: '—', icon: '3' }
  ];

  recentSales: any[] = [];
  leaderboard: any[] = [];

  // Trend chart data - Fix: Updated type to include date and amount
  trendPoints = '';
  trendDataPoints: { x: number; y: number; date: string; amount: number }[] = [];
  trendPath = false;

  currentAgentId = '';

  constructor(
    private salesService: SalesService,
    private incentivesService: IncentivesService,
    private leaderboardService: LeaderboardService,
    private authService: AuthService,
    private router: Router
  ) {}

  async ngOnInit(): Promise<void> {
    const user = this.authService.currentUser();
    if (user) this.currentAgentId = user.agentid;

    try {
      const sales = await this.salesService.getAllSalesWithDetails();
      const agentSales = sales.filter(s => s.agentid === this.currentAgentId);
      const total = agentSales.reduce((sum, s) => sum + (s.amount || 0), 0);

      // Get incentives for current agent
      const allIncentives = await this.incentivesService.getAllIncentivesWithDetails();
      const agentIncentives = allIncentives.filter((i: any) => i.agentid === this.currentAgentId);
      const totalBonus = agentIncentives.reduce((sum: number, i: any) => sum + (i.bonus || 0), 0);

      this.stats[0].value = `₹${total.toLocaleString()}`;
      this.stats[1].value = `₹${totalBonus.toLocaleString()}`;

      this.recentSales = agentSales.slice(0, 4);

      // build leaderboard: sum sales per agent, sort desc
      const totalsByAgent: Record<string, { name: string; score: number }> = {};
      for (const s of sales || []) {
        if (!totalsByAgent[s.agentid]) {
          totalsByAgent[s.agentid] = { name: s.agentName || s.agentid, score: 0 };
        }
        totalsByAgent[s.agentid].score += (s.amount || 0);
      }
      this.leaderboard = Object.entries(totalsByAgent)
        .map(([agentid, data]) => ({ rank: 0, name: data.name, score: data.score }))
        .sort((a, b) => b.score - a.score)
        .map((item, i) => ({ ...item, rank: i + 1 }));

      // Set current agent's rank
      const currentAgentEntry = this.leaderboard.find(l => l.name === user?.name);
      if (currentAgentEntry) {
        this.stats[2].value = currentAgentEntry.rank.toString();
        this.stats[2].icon = currentAgentEntry.rank <= 3 ? ['🥇', '🥈', '🥉'][currentAgentEntry.rank - 1] : currentAgentEntry.rank.toString();
      }

      // build sales trend chart for current agent
      this.buildSalesTrendChart(agentSales);
    } catch (e) {
      console.error('Dashboard load error', e);
    }
  }

  /**
   * Build sales trend chart data from sales grouped by date
   */
  private buildSalesTrendChart(sales: any[]): void {
    if (!sales || sales.length === 0) {
      this.trendPath = false;
      return;
    }

    // Group sales by date and sum amounts
    const salesByDate: Record<string, number> = {};
    const dates: string[] = [];
    for (const s of sales) {
      const date = s.saleDate || '';
      if (!salesByDate[date]) {
        salesByDate[date] = 0;
        dates.push(date);
      }
      salesByDate[date] += (s.amount || 0);
    }

    // Sort dates chronologically
    dates.sort((a, b) => {
      const aDate = new Date(a).getTime();
      const bDate = new Date(b).getTime();
      return aDate - bDate;
    });

    if (dates.length === 0) {
      this.trendPath = false;
      return;
    }

    // Find min/max for scaling
    const amounts = dates.map(d => salesByDate[d]);
    const maxAmount = Math.max(...amounts);
    const minAmount = Math.min(...amounts);
    const amountRange = maxAmount - minAmount || 1;

    // SVG viewBox: 0 0 400 200 (increased height for axis labels)
    const svgWidth = 400;
    const svgHeight = 200;
    const padding = 40;
    const chartWidth = svgWidth - 2 * padding;
    const chartHeight = svgHeight - 2 * padding - 20; // Leave space for axis labels

    // Calculate points for polyline
    const points: { x: number; y: number; date: string; amount: number }[] = [];
    dates.forEach((date, idx) => {
      const x = padding + (idx / (dates.length - 1 || 1)) * chartWidth;
      const amount = salesByDate[date];
      // Normalize: 0 = bottom, maxAmount = top
      const normalized = (amount - minAmount) / amountRange;
      const y = svgHeight - padding - 20 - normalized * chartHeight;
      points.push({ x, y, date, amount });
    });

    // Build polyline points string
    this.trendPoints = points.map(p => `${p.x},${p.y}`).join(' ');
    this.trendDataPoints = points;
    this.trendPath = true;
  }

  // navigate to agent-sales page (Add Sale or View All Sales)
  goToSales(): void {
    this.router.navigate(['/agent/sales']);
  }

  // navigate to incentives page
  goToIncentives(): void {
    this.router.navigate(['/agent/incentives']);
  }
}
