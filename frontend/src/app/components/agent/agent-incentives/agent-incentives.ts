import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sidebar } from '../../sidebar/sidebar';
import { IncentivesService } from '../../../services/incentives.service';
import { SalesService } from '../../../services/sales.service';
import { Incentive } from '../../../models/incentive.model';
import { AuthService } from '../../../services/auth.service';

interface IncentiveWithDetails extends Incentive {
  agentName?: string;
  bonus?: number;
}

@Component({
  selector: 'app-agent-incentives',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './agent-incentives.html',
  styleUrl: './agent-incentives.css',
})
export class AgentIncentives implements OnInit {
  incentives: IncentiveWithDetails[] = [];
  filteredIncentives: IncentiveWithDetails[] = [];

  totalIncentives = 0;
  totalBonus = 0;
  pendingCount = 0;

  selectedStatus = 'All';
  startDate = '';
  endDate = '';

  page = 1;
  pageSize = 6;

  loading = true;

  currentAgentId = '';

  constructor(
    private incentivesService: IncentivesService,
    private salesService: SalesService,
    private authService: AuthService
  ) {}

  async ngOnInit(): Promise<void> {
    const user = this.authService.currentUser();
    if (user) this.currentAgentId = user.agentid;
    await this.loadIncentivesData();
  }

  private async loadIncentivesData(): Promise<void> {
    try {
      this.loading = true;

      //  Force reload from sales service to pick up new/updated sales
      const sales = await this.salesService.getSales();


      // Now get incentives which are derived from completed sales
      const allIncentives = await this.incentivesService.getIncentivesByAgentId(this.currentAgentId);
      this.incentives = allIncentives;


      // Calculate totals for current agent (include all incentives, not just completed)
      this.totalIncentives = await this.incentivesService.getTotalIncentivesAmount();
      this.totalBonus = await this.incentivesService.getTotalBonusAmount();
      this.pendingCount = await this.incentivesService.getPendingCount();



      this.applyFilters();
    } catch (error) {
      console.error('Error loading incentives data:', error);
    } finally {
      this.loading = false;
    }
  }

  applyFilters(): void {
    let filtered = [...this.incentives];

    // Filter by status
    if (this.selectedStatus !== 'All') {
      filtered = filtered.filter(i => String(i.status || '').toLowerCase() === this.selectedStatus.toLowerCase());
    }


    this.filteredIncentives = filtered;
    this.page = 1; // Reset to first page
  }

  private parseDate(dateStr: string): Date {
    // Parse mm/dd/yyyy or ISO date format
    if (dateStr.includes('-')) {
      // ISO format: YYYY-MM-DD
      return new Date(dateStr);
    }
    // mm/dd/yyyy format
    const [month, day, year] = dateStr.split('/').map(Number);
    return new Date(year, month - 1, day);
  }

  onStatusChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.selectedStatus = target.value;
    this.applyFilters();
  }


  get pagedIncentives(): IncentiveWithDetails[] {
    const start = (this.page - 1) * this.pageSize;
    return this.filteredIncentives.slice(start, start + this.pageSize);
  }

  prevPage(): void {
    if (this.page > 1) this.page--;
  }

  nextPage(): void {
    if (this.page * this.pageSize < this.filteredIncentives.length) this.page++;
  }
}
