import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { AuthService } from './auth.service';

export interface PolicyDto {
  policyId?: string;
  policy_id?: string;
  policyid?: string;
  policyCode?: string;
  name?: string;
  id?: number;
}

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private apiUrl = 'http://localhost:8080/policies';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return { headers };
  }

  async getPolicies(): Promise<PolicyDto[]> {
    try {
      return await firstValueFrom(this.http.get<PolicyDto[]>(this.apiUrl, this.authHeaders()));
    } catch (err) {
      console.error('PolicyService.getPolicies failed', err);
      throw err;
    }
  }

  async createPolicy(payload: PolicyDto): Promise<PolicyDto> {
    try {
      return await firstValueFrom(this.http.post<PolicyDto>(this.apiUrl, payload, this.authHeaders()));
    } catch (err) {
      console.error('PolicyService.createPolicy failed', err);
      throw err;
    }
  }
}
