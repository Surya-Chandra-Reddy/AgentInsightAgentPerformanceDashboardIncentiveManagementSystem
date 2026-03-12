import { Injectable, Optional } from '@angular/core'; 
import { Sale } from '../models/sale.model'; 
import { Agent } from '../models/agent.model'; 
import { Incentive } from '../models/incentive.model'; 
import { IncentivesService } from './incentives.service'; 
import { firstValueFrom } from 'rxjs'; 
import { HttpClient, HttpParams } from '@angular/common/http'; 

@Injectable({ // 
  providedIn: 'root' 
})
export class AdminSalesService { // Main service class for admin sales operations.
  private apiUrl = 'http://localhost:8080';

  constructor(@Optional() private http?: HttpClient, @Optional() private incentivesService?: IncentivesService) {} // Constructor with optional HttpClient and IncentivesService dependencies.

  // Private method to build query parameters for HTTP requests.
  private qsParams(paramsObj?: Record<string, string | number | boolean>): HttpParams | undefined {
    if (!paramsObj) return undefined;
    return new HttpParams({ fromObject: Object.entries(paramsObj).reduce((acc, [k, v]) => ({ ...acc, [k]: String(v) }), {}) });
  }

  // Private async method for GET requests with fallback to fetch.
  private async get<T>(path: string, paramsObj?: Record<string, string | number | boolean>): Promise<T> {
    if (this.http) {
      try {
        return await firstValueFrom(this.http.get<T>(`${this.apiUrl}${path}`, { params: this.qsParams(paramsObj) }));
      } catch (err) {
        console.error(`GET ${path} failed (HttpClient)`, err);
        throw err;
      }
    }
    // fetch fallback
    const qs = paramsObj ? '?' + Object.entries(paramsObj).map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(String(v))}`).join('&') : '';
    const res = await fetch(`${this.apiUrl}${path}${qs}`);
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(`GET ${path} failed (fetch) - ${res.status} ${res.statusText} ${text}`);
    }
    return (await res.json()) as T;
  }

  // Private async method for PATCH requests with fallback to fetch.
  private async patch<T>(path: string, body: any): Promise<T> {
    if (this.http) {
      try {
        return await firstValueFrom(this.http.patch<T>(`${this.apiUrl}${path}`, body, { headers: { 'Content-Type': 'application/json' } }));
      } catch (err) {
        console.error(`PATCH ${path} failed (HttpClient)`, err);
        throw err;
      }
    }

    const res = await fetch(`${this.apiUrl}${path}`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(`PATCH ${path} failed (fetch) - ${res.status} ${res.statusText} ${text}`);
    }
    return (await res.json()) as T;
  }

  /**
   * Get all sales with agent details for admin view
   */
  async getAllSalesWithAgentDetails(): Promise<any[]> { // Method to retrieve all sales with agent details.
    const sales = await this.get<Sale[]>('/sales'); // Fetches sales data.
    const agents = await this.get<Agent[]>('/users'); // Fetches agents data.

    return (sales || []).map((sale) => ({ // Maps sales to include agent names.
      ...sale,
      agentName: agents.find((a) => a.agentid === sale.agentid)?.name || 'Unknown Agent' // Finds agent name or defaults.
    }));
  }

  /**
   * Get paginated sales with agent details for admin view
   */
  async getPaginatedSalesWithAgentDetails(page: number = 0, size: number = 10): Promise<any> {
    const salesPage = await this.get<any>('/sales/salesdetails/paginated', { page, size });
    const agents = await this.get<Agent[]>('/users');

    const content = (salesPage.content || []).map((sale: any) => ({
      saleid: sale.saleid,
      agentid: sale.agentid,
      agentName: agents.find((a) => a.agentid === sale.agentid)?.name || 'Unknown Agent',
      saleamount: sale.amount,
      saledate: sale.saleDate,
      status: sale.status
    }));

    return {
      ...salesPage,
      content
    };
  }
  async updateSaleStatus(saleid: string, status: string): Promise<Sale> { // Method to update sale status and potentially create incentive.
    try {
      const existing = await this.get<Sale>(`/sales/${saleid}`); // Fetches sale by ID.
      if (!existing) { // Checks if sale exists.
        throw new Error(`Sale not found for saleid ${saleid}`); // Throws error if not found.
      }
      const updatedSale = await this.patch<Sale>(`/sales/${saleid}`, { status }); // Updates sale status.

      // If status is 'Completed' and incentivesService is available, create incentive
      if (status === 'Completed' && this.incentivesService) {
        const incentiveAmount = existing.amount * 0.10; // use saleamount, not amount
        const incentive = {
          agentid: existing.agentid,              // must be present
          amount: incentiveAmount,
          calculationdate: new Date().toISOString(), // ISO format
          status: 'Pending'
        };
        await this.incentivesService.createIncentive(incentive);
      }


      if (this.incentivesService) {
        this.incentivesService.emitIncentivesUpdated();
      }

      return updatedSale; // Returns updated sale.
    } catch (err) {
      console.error('updateSaleStatus error', err); // Logs error.
      throw err; // Re-throws error.
    }
  }
}
