import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Leaderboard } from '../models/leaderboard.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  private apiUrl = 'http://localhost:8080/leaderboard';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private authHeaders(): { headers?: HttpHeaders } {
    const token = this.auth.getToken();
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }
    return { headers };
  }

  async getLeaderboardData(): Promise<Leaderboard[]> {
    return firstValueFrom(this.http.get<Leaderboard[]>(this.apiUrl, this.authHeaders()));
  }

  async calculateLeaderboard(): Promise<void> {
    return firstValueFrom(this.http.post<void>(`${this.apiUrl}/calculateLeaderboard`, {}, this.authHeaders()));
  }

  async updateEntry(entryId: string, data: Partial<Leaderboard>): Promise<Leaderboard> {
    return firstValueFrom(this.http.put<Leaderboard>(`${this.apiUrl}/${entryId}`, data, this.authHeaders()));
  }

  async deleteEntry(entryId: string): Promise<void> {
    return firstValueFrom(this.http.delete<void>(`${this.apiUrl}/${entryId}`, this.authHeaders()));
  }
}