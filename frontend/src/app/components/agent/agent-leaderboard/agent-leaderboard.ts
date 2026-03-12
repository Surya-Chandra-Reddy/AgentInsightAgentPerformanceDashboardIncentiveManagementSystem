import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sidebar } from '../../sidebar/sidebar';
import { LeaderboardService } from '../../../services/leaderboard.service';
import { Leaderboard } from '../../../models/leaderboard.model'; 
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-agent-leaderboard',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './agent-leaderboard.html',
  styleUrl: './agent-leaderboard.css'
})
export class AgentLeaderboard implements OnInit {
 
  leaderboardEntries: Leaderboard[] = [];
  currentAgentId = '';
  isLoading = false;
 

  constructor(
    private leaderboardService: LeaderboardService, 
    private authService: AuthService
  ) {}

  async ngOnInit() {
    const user = this.authService.currentUser();
    if (user) this.currentAgentId = user.agentid;

    await this.refreshAndLoad();
  }

 
  async refreshAndLoad() {
    this.isLoading = true;
    try {
      await this.leaderboardService.calculateLeaderboard();
      
      await this.loadLeaderboard();
    } catch (error) {
      console.error('Failed to sync leaderboard:', error);
    } finally {
      this.isLoading = false;
    }
  }

  async loadLeaderboard() {
    try {
      const entries = await this.leaderboardService.getLeaderboardData()
      
      this.leaderboardEntries = entries.map(entry => ({
        ...entry,
        isCurrentAgent: entry.agentId === this.currentAgentId
      }));
      
      console.log('Leaderboard updated:', this.leaderboardEntries);
    } catch (error) {
      console.error('Error loading leaderboard:', error);
    }
  }

  getRankIcon(rank: number): string {
    switch (rank) {
      case 1: return '🥇';
      case 2: return '🥈';
      case 3: return '🥉';
      default: return `#${rank}`;
    }
  }

  trackByEntryId(index: number, item: Leaderboard): string {
    return item.entryId;
  }

}