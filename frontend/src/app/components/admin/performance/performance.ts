import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { AdminSidebar } from "../admin-sidebar/admin-sidebar";
import { AgentService } from '../../../services/agent.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-performance',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AdminSidebar],
  templateUrl: './performance.html',
  styleUrls: ['./performance.css'],
})
export class Performance implements OnInit {
  filterForm: FormGroup;
  selectedAgentData: any = null;
  error = signal<string>('');

  constructor(
    private fb: FormBuilder,
    private agentService: AgentService
  ) {
    this.filterForm = this.fb.group({
      agentId: ['']
    });
  }

  ngOnInit(): void {}

  async applyFilters(): Promise<void> {
    this.error.set('');
    const agentId = this.filterForm.value.agentId?.trim();

    if (!agentId) {
      this.selectedAgentData = null;
      return;
    }

    try {
      this.selectedAgentData = await this.agentService.getAgentPerformanceById(agentId);
      console.log('Received Data:', this.selectedAgentData);
    } catch (err) {
      this.selectedAgentData = null;
      console.error('Error fetching performance data:', err);

      // Extract the custom message from the backend response
      if (err instanceof HttpErrorResponse) {
        // Accesses the 'message' field from your GlobalExceptionHandler Map
        const backendMessage = err.error?.message || err.error;
        this.error.set(backendMessage || 'An unexpected error occurred');
      } else {
        this.error.set('Could not connect to the server');
      }
    }
  }
}