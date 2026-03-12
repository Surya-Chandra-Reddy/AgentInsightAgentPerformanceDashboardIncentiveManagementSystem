import { Injectable, Optional } from '@angular/core'; // Imports Injectable and Optional decorators from Angular core for dependency injection.
import { Agent } from '../models/agent.model'; // Imports the Agent model for type definitions.
import { firstValueFrom } from 'rxjs'; // Imports firstValueFrom to convert observables to promises.
import { HttpClient, HttpParams } from '@angular/common/http'; // Imports HttpClient and HttpParams for HTTP requests.
 
@Injectable({
  providedIn: 'root'
})
export class AgentService {
  private apiUrl = 'http://localhost:8080';
 
  constructor(private http: HttpClient) {}
 
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
 
  // Private async method for POST requests with fallback to fetch.
  private async post<T>(path: string, body: any): Promise<T> {
    if (this.http) {
      try {
        return await firstValueFrom(this.http.post<T>(`${this.apiUrl}${path}`, body));
      } catch (err: any) {
        console.error(`POST ${path} failed (HttpClient)`, err);
        // Extract error message from response
        if (err.error?.message) {
          throw new Error(err.error.message);
        }
        throw err;
      }
    }
 
    // fetch fallback
    const res = await fetch(`${this.apiUrl}${path}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(`POST ${path} failed (fetch) - ${res.status} ${res.statusText} ${text}`);
    }
    return (await res.json()) as T;
  }
 
  // Private async method for PATCH requests with fallback to fetch.
  private async patch<T>(path: string, body: any): Promise<T> {
    if (this.http) {
      try {
        return await firstValueFrom(this.http.patch<T>(`${this.apiUrl}${path}`, body));
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
 
  // Private async method for PUT requests with fallback to fetch.
private async put<T>(path: string, body: any): Promise<T> {
  if (this.http) {
    try {
      return await firstValueFrom(this.http.put<T>(`${this.apiUrl}${path}`, body));
    } catch (err) {
      console.error(`PUT ${path} failed (HttpClient)`, err);
      throw err;
    }
  }
 
  const res = await fetch(`${this.apiUrl}${path}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body)
  });
  if (!res.ok) {
    const text = await res.text().catch(() => '');
    throw new Error(`PUT ${path} failed (fetch) - ${res.status} ${res.statusText} ${text}`);
  }
  return (await res.json()) as T;
}
 
 
  // Private async method for DELETE requests with fallback to fetch.
  private async deleteReq<T>(path: string): Promise<T> {
    if (this.http) {
      try {
        return await firstValueFrom(this.http.delete<T>(`${this.apiUrl}${path}`));
      } catch (err) {
        console.error(`DELETE ${path} failed (HttpClient)`, err);
        throw err;
      }
    }
 
    const res = await fetch(`${this.apiUrl}${path}`, { method: 'DELETE' });
    if (!res.ok) {
      const text = await res.text().catch(() => '');
      throw new Error(`DELETE ${path} failed (fetch) - ${res.status} ${res.statusText} ${text}`);
    }
    // some deletes return empty body
    const ct = res.headers.get('content-type') || '';
    return ct.includes('application/json') ? (await res.json()) as T : (undefined as unknown as T);
  }
 
  getAllAgents(): Promise<Agent[]> {
    return this.get<Agent[]>('/agent/loadagents');
  }

  //Method used in performance.ts
  getAgentPerformanceById(agentId: string): Promise<any> {
    console.log(`Fetching performance data for agent ID: ${agentId}`);
    return this.get<any>(`/performance/${agentId}`);
  }

  //Method used in reports.ts
  getAgentReport(): Promise<any[]> {
    return this.get<any[]>('/reports/agent-performance');
  }


  async getAgentsData(): Promise<Agent[]> {
    return this.get<Agent[]>('/users');
  }
 
  async getAgents(): Promise<Agent[]> {
    const agents = await this.get<any[]>('/users');
    // Normalize: map 'id' to 'agentid' if needed, and filter out admin roles
    return ((agents || [])
      .filter(agent => agent.role !== 'admin') // Exclude admin roles
      .map(agent => ({
        ...agent,
        agentid: agent.agentid || agent.id?.toString()
      }))
      .sort((a, b) => a.agentid.localeCompare(b.agentid))) as Agent[]; // Sort by agentid in ascending order
  }
 
  /**
   * Check if an agent with the given agentid already exists
   */
  async agentIdExists(agentid: string): Promise<boolean> {
    try {
      const agents = await this.get<Agent[]>('/users');
      return (agents || []).some(agent => agent.agentid === agentid);
    } catch (err) {
      console.error('Error checking agent ID', err);
      return false;
    }
  }
 
  /**
   * Create a new agent
   */
  async createAgent(agent: Agent): Promise<Agent> {
    try {
      console.log('📤 Sending create agent request:', agent);
      const response = await this.post<any>('/agent/addagent', agent);
      console.log('📥 Create agent response:', response);
     
      if (!response) {
        throw new Error('No response from server');
      }
     
      if (!response.agentid) {
        throw new Error('Server did not return agent ID');
      }
     
      // Map response to Agent model
      const createdAgent: Agent = {
        agentid: response.agentid,
        name: response.name || agent.name,
        email: response.email || agent.email,
        phone: response.phone || agent.phone,
        role: response.role || 'AGENT',
        password: agent.password
      };
     
      console.log('✅ Agent mapped successfully:', createdAgent);
      return createdAgent;
    } catch (err) {
      console.error('❌ createAgent error:', err);
      throw err;
    }
  }
 
  /**
   * Update an existing agent
   */
  async updateAgent(agent: Agent): Promise<Agent> {
    try {
      console.log('📤 Sending update agent request:', agent);
      const response = await this.patch<any>(`/agent/update/${agent.agentid}`, agent);
      console.log('📥 Update agent response:', response);
     
      if (!response) {
        throw new Error('No response from server');
      }
     
      if (!response.agentid) {
        throw new Error('Server did not return agent ID');
      }
     
      // Map response to Agent model
      const updatedAgent: Agent = {
        agentid: response.agentid,
        name: response.name || agent.name,
        email: response.email || agent.email,
        phone: response.phone || agent.phone,
        role: response.role || agent.role,
        password: agent.password
      };
     
      console.log('✅ Agent mapped successfully:', updatedAgent);
      return updatedAgent;
    } catch (err) {
      console.error('❌ updateAgent error:', err);
      throw err;
    }
  }
 
 
  /**
   * Delete agent by agentid
   */
  async deleteAgent(agentid: string): Promise<void> { // Method to delete an agent by ID.
    try {
      await this.deleteReq<void>(`/agent/delete/${agentid}`); // Deletes the agent directly by agentid.
    } catch (err) {
      console.error('deleteAgent error', err); // Logs error.
      throw err; // Re-throws error.
    }
  }
}