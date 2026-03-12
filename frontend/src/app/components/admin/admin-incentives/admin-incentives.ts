import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IncentivesService } from '../../../services/incentives.service';
import { Subscription } from 'rxjs';

interface IncentiveWithDetails {
  incentiveid: string;
  agentid: string;
  agentName?: string;
  amount: number;
  calculationdate: string;
  status: string;
}

@Component({
  selector: 'app-admin-incentives',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [IncentivesService],
  templateUrl: './admin-incentives.html',
  styleUrl: './admin-incentives.css'
})
export class AdminIncentives implements OnInit, OnDestroy {
  incentives: IncentiveWithDetails[] = [];
  loading = true;
  updatingIncentiveId: string | null = null;
  selectedIncentive: IncentiveWithDetails | null = null;
  selectedStatus: string = '';
  statusOptions: string[] = ['Paid', 'Cancelled', 'Pending'];
  showModal: boolean = false;

  // Pagination properties
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  private subscription: Subscription = new Subscription();

  constructor(private incentivesService: IncentivesService) {}

  ngOnInit(): void {
    this.loadIncentives();
    this.subscription.add(
      this.incentivesService.incentivesUpdated$.subscribe(() => {
        this.loadIncentives();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  async loadIncentives(): Promise<void> {
    try {
      this.loading = true;
      const allIncentives = await this.incentivesService.getAllIncentivesWithDetails();
      this.incentives = allIncentives;
    } catch (error) {
      console.error('Failed to load incentives', error);
    } finally {
      this.loading = false;
    }
  }

  async refreshIncentives(): Promise<void> {
    await this.loadIncentives();
  }

  openStatusModal(incentive: IncentiveWithDetails): void {
    this.selectedIncentive = incentive;
    this.selectedStatus = incentive.status;
    this.showModal = true;
  }

  closeStatusModal(): void {
    this.selectedIncentive = null;
    this.selectedStatus = '';
    this.showModal = false;
  }

  async confirmUpdate(): Promise<void> {
    if (!this.selectedIncentive || !this.selectedStatus || this.selectedStatus === this.selectedIncentive.status) {
      this.closeStatusModal();
      return;
    }

    try {
      this.updatingIncentiveId = this.selectedIncentive.incentiveid;
      await this.incentivesService.updateIncentiveStatus(this.selectedIncentive.incentiveid, this.selectedStatus);

      // ✅ Update the incentive in the array
      const index = this.incentives.findIndex(i => i.incentiveid === this.selectedIncentive!.incentiveid);
      if (index !== -1) {
        this.incentives[index] = {
          ...this.incentives[index],
          status: this.selectedStatus
        };
      }

      this.closeStatusModal();
    } catch (error) {
      console.error('Failed to update status', error);
      alert('Failed to update status. Please try again.');
    } finally {
      this.updatingIncentiveId = null;
    }
  }

  getStatusClass(status: string): string {
    switch (status?.toLowerCase()) {
      case 'paid':
        return 'bg-success';
      case 'pending':
        return 'bg-warning text-dark';
      case 'cancelled':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }
}
