import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SalesService } from '../../../services/sales.service';
import { IncentivesService } from '../../../services/incentives.service';
import { AgentService } from '../../../services/agent.service';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './reports.html',
  styleUrls: ['./reports.css']
})
export class Reports implements OnInit {
  reportForm: FormGroup;
  reportType: string = '';
  reportData: any[] = [];
  agentData: any[] = [];
  totalSales: number = 0;
  totalIncentives: number = 0;
  generatedDate: string = '';
  generatedTime: string = '';

  constructor(
    private fb: FormBuilder,
    private salesService: SalesService,
    private incentivesService: IncentivesService,
    private agentService: AgentService
  ) {
    this.reportForm = this.fb.group({
      reportType: ['']
    });
  }

  ngOnInit(): void {}

  async generateReport(): Promise<void> {
    this.reportType = this.reportForm.value.reportType;
    const now = new Date();
    this.generatedDate = now.toLocaleDateString();
    this.generatedTime = now.toLocaleTimeString();

    if (this.reportType === 'sales') {
      await this.generateSalesReport();
    } else if (this.reportType === 'agent') {
      console.log('Generating agent report');
      await this.generateAgentReport();
      console.log('Agent Data:', this.agentData);
    } else if (this.reportType === 'incentives') {
      await this.generateIncentivesReport();
    }
  }

  async generateSalesReport(): Promise<void> {
    try {
      const sales = await this.salesService.getSalesForReports();
      console.log('Fetched Sales:', sales);
      this.reportData = sales;
      this.totalSales = await this.salesService.getTotalSalesAmount();
      console.log('Sales Data:', this.reportData);
    } catch (error) {
      console.error('Error generating sales report:', error);
    }
  }

  // reports.ts
  async generateAgentReport(): Promise<void> {
    try {
      // No more manual filtering or reduce() logic here!
      this.agentData = await this.agentService.getAgentReport();
    } catch (error) {
      console.error('Error fetching agent report:', error);
    }
  }

  async generateIncentivesReport(): Promise<void> {
    try {
      const incentives = await this.incentivesService.getIncentives();
      this.reportData = incentives;
      this.totalIncentives = incentives.reduce((sum: number, inc: any) => sum + inc.amount, 0);
    } catch (error) {
      console.error('Error generating incentives report:', error);
    }
  }

  getReportTitle(): string {
    switch (this.reportType) {
      case 'sales':
        return 'Sales Report';
      case 'agent':
        return 'Agent Performance Report';
      case 'incentives':
        return 'Incentives Report';
      default:
        return 'Report';
    }
  }

  downloadReport(): void {
    // Placeholder for download functionality
    alert('Download functionality not implemented yet.');
  }



}
