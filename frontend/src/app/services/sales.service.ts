import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Sale } from '../models/sale.model';
import { Agent } from '../models/agent.model';
import { AuthService } from './auth.service'; // added
 
@Injectable({
  providedIn: 'root'
})
export class SalesService {
 
  private apiUrl = 'http://localhost:8080/sales';
  private performanceApiUrl = 'http://localhost:8080/performance/allsales';
  private reportsApiUrl = 'http://localhost:8080/reports/allsales';

  private usersApiUrl = 'http://localhost:8080/users';
  private policiesApiUrl = 'http://localhost:8080/policies';
 
  constructor(private http: HttpClient, private auth: AuthService) {} // inject AuthService
 
  // helper to attach Authorization header when token present
  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return { headers };
  }
 
  /**
   * Get all sales (Promise)
   */
  async getSales(): Promise<Sale[]> {
    return firstValueFrom(this.http.get<Sale[]>(this.apiUrl, this.authHeaders()));
  }

  async getAllSalesForPerformance(): Promise<Sale[]> {
    return firstValueFrom(this.http.get<Sale[]>(this.performanceApiUrl, this.authHeaders()));
  }


   async getSalesForReports(): Promise<Sale[]> {
    return firstValueFrom(this.http.get<Sale[]>(this.reportsApiUrl, this.authHeaders()));
  }
 
  /**
   * Get sales by agent ID (Promise)
   */
  async getSalesByAgentId(agentid: string): Promise<Sale[]> {
    // try backend route first
    try {
      return firstValueFrom(this.http.get<Sale[]>(`${this.apiUrl}/agent/${agentid}`, this.authHeaders()));
    } catch {
      const all = await this.getSales();
      return (all || []).filter(s => s.agentid === agentid);
    }
  }
 
  /**
   * Create a new sale (POST /sales/addsales) — returns created sale
   */
  async createSale(sale: any): Promise<any> {
    return firstValueFrom(this.http.post<any>(`${this.apiUrl}/addsales`, sale, this.authHeaders()));
  }
 
  /**
   * Update an existing sale (PUT /sales/updatesale) — returns updated sale or response
   */
 async updateSale(saleid: string, sale: any): Promise<any> {
  console.log('Updating sale with ID:', saleid, 'and data:', sale);
  return firstValueFrom(
    this.http.patch<any>(`${this.apiUrl}/updatesale/${saleid}`, sale, this.authHeaders())
  );
}

 
  /**
   * Delete sale by saleid (DELETE /sales/deletesale/:saleid)
   */
  async deleteSale(saleid: string): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.apiUrl}/deletesale/${saleid}`, this.authHeaders()));
  }
 
  /**
   * Get all agents
   */
  async getAgents(): Promise<Agent[]> {
    return firstValueFrom(this.http.get<Agent[]>(this.usersApiUrl, this.authHeaders()));
  }
 
  /**
   * Get all policies
   */
  async getPolicies(): Promise<{ policyid?: string; policy_id?: string; policyId?: string; name: string }[]> {
    return firstValueFrom(this.http.get<{ policyid?: string; policy_id?: string; policyId?: string; name: string }[]>(this.policiesApiUrl, this.authHeaders()));
  }
 
  /**
   * Get all sales with agent and policy details
   */
  async getAllSalesWithDetails(): Promise<any[]> {
    // fetch sales, agents and policies, then attach agentName and policyName
    const [sales, agents, policies] = await Promise.all([
      this.getSales(),
      this.getAgents(),
      this.getPolicies()
    ]);
 
    const agentsMap = (agents || []).reduce<Record<string, Agent>>((acc, a) => {
      if (a.agentid) acc[a.agentid] = a;
      return acc;
    }, {});
 
    const policiesMap = (policies || []).reduce<Record<string, { name: string }>>((acc, p) => {
      const id = p.policyid || p.policy_id || p.policyId || '';
      if (id) acc[id] = { name: p.name || '' };
      return acc;
    }, {});
 
    return (sales || []).map(s => ({
      ...s,
      agentName: agentsMap[s.agentid]?.name || '',
      policyName: policiesMap[s.policyid]?.name || ''
    }));
  }
 
  /**
   * Get conversion rate (placeholder)
   */
  async getConversionRate(): Promise<number> {
    return firstValueFrom(this.http.get<number>(`${this.apiUrl}/getconversionrate`, this.authHeaders()));
  }
 
  /**
   * Get total sales amount
   */
  async getTotalSalesAmount(): Promise<number> {
    return firstValueFrom(this.http.get<number>(`${this.apiUrl}/gettotalsaleamount`, this.authHeaders()));
  }
}
 