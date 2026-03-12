import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Incentive } from '../models/incentive.model';
import { firstValueFrom, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class IncentivesService {
  private http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/incentives';

  // Observable for components to listen for data refreshes
  private incentivesUpdated = new Subject<void>();
  incentivesUpdated$ = this.incentivesUpdated.asObservable();
  emitIncentivesUpdated(): void {
    this.incentivesUpdated.next();
  }
  // Admin gets all incentives with details
  async getAllIncentives(): Promise<Incentive[]> {
    return await firstValueFrom(this.http.get<Incentive[]>(this.apiUrl));
  }
  // Agent: Get incentives for a specific agent
  async getIncentivesByAgentId(agentId: string): Promise<Incentive[]> {
    return await firstValueFrom(this.http.get<Incentive[]>(`${this.apiUrl}/agent/${agentId}`));
  }
  // Admin: Update incentive status
  async updateIncentiveStatus(id: string, status: string): Promise<Incentive> {
    return await firstValueFrom(
      this.http.patch<Incentive>(`${this.apiUrl}/${id}/status`, {}, {
        params: { status }
      })
    );
  }
  // Analytics: Fetch total bonus amount
  async getTotalBonusAmount(): Promise<number> {
    return await firstValueFrom(this.http.get<number>(`${this.apiUrl}/analytics/total-bonus`));
  }
  // Analytics: Fetch pending count
  async getPendingCount(): Promise<number> {
    return await firstValueFrom(this.http.get<number>(`${this.apiUrl}/analytics/pending-count`));
  }
  // Create a new incentive
  async createIncentive(incentive: Partial<Incentive>): Promise<Incentive> {
    const result = await firstValueFrom(this.http.post<Incentive>(`${this.apiUrl}/create`, incentive));
    this.emitIncentivesUpdated();
    return result;
  }
  // Get all incentives with details (includes agentName and bonus)
  async getAllIncentivesWithDetails(): Promise<Incentive[]> {
    return await firstValueFrom(this.http.get<Incentive[]>(this.apiUrl));
  }
  // Get basic incentives (without details)
  async getIncentives(): Promise<Incentive[]> {
    return await firstValueFrom(this.http.get<Incentive[]>(`${this.apiUrl}/basic`));
  }
  // Get total incentives amount
  async getTotalIncentivesAmount(): Promise<number> {
    return await firstValueFrom(this.http.get<number>(`${this.apiUrl}/analytics/total-amount`));
  }
}
