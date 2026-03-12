import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sidebar } from '../../sidebar/sidebar';
import { AuthService } from '../../../services/auth.service';
import { AgentService } from '../../../services/agent.service';
import { Agent } from '../../../models/agent.model';

@Component({
  selector: 'app-agent-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './agent-profile.html',
  styleUrl: './agent-profile.css',
})
export class AgentProfile implements OnInit {
  isReadOnly = true;
  original: Agent | null = null;
  model: Agent = { agentid: '', name: '', email: '', phone: '', role: 'AGENT' };
  saving = false;
  message = '';

  constructor(private authService: AuthService, private agentService: AgentService) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.loadUser(user.agentid);
    }
  }

  async loadUser(agentid: string): Promise<void> {
    const current = this.authService.currentUser();
    if (current) {
      this.original = { ...current };
      this.model = { ...current };
    }
  }

  async save(): Promise<void> {
    this.saving = true;
    this.message = 'Saving changes...';
    try {
      const dataToSend = {
        ...this.model,
        role: this.model.role.toUpperCase()
      };

      const updated = await this.agentService.updateAgent(dataToSend);
      this.original = { ...updated };
      this.authService.setCurrentUser(updated);
      this.message = 'Profile saved successfully!';
      this.isReadOnly = true;
    } catch (err) {
      console.error("Full Error Object:", err);
      this.message = 'Failed to save: Check console for details';
    } finally {
      this.saving = false;
    }
  }

  cancel(): void {
    if (this.original) {
      this.model = { ...this.original };
    }
    this.isReadOnly = true;
    this.showMessage('Changes discarded'); 
  }

  private showMessage(msg: string): void {
    this.message = msg;
    setTimeout(() => {
      this.message = '';
    }, 3000); 
  }

  toggleEdit(): void {
    this.isReadOnly = !this.isReadOnly;
    if (!this.isReadOnly) {
      this.message = ''; 
    }
  }
}