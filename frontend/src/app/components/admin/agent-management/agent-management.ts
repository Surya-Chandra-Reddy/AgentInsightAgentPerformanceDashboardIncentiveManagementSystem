import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Agent } from '../../../models/agent.model';
import { AgentService } from '../../../services/agent.service';
 
@Component({
  selector: 'app-agent-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './agent-management.html',
  styleUrls: ['./agent-management.css'],
})
export class AgentManagement implements OnInit {
  agents: Agent[] = [];
  selectedAgent: Agent = { agentid: '', name: '', email: '', phone: '', role: '', password: '' };
 
  // Patterns
  namePattern: string = '^[A-Za-z . ]+$';       // alphabets + spaces only
  phonePattern: string = '^(\\d{10})$';       // exactly 10 digits, numeric only
  passwordPattern: string = '^(?=.*[A-Za-z])(?=.*\\d).+$'; // at least one letter and one number
 
  isEditing = false; // controls visibility of the edit form
  isNew = false;     // distinguishes add vs edit
  formSubmitted = false;
  emailExists = false; // flag to track email uniqueness errors
  agentIdExists = false;
  successMessage: string = '';
  errorMessage: string = '';
 
  constructor(private agentService: AgentService) {}
 
  ngOnInit(): void {
    this.loadAgents();
  }
 
  async loadAgents(): Promise<void> {
    try {
      this.agents = await this.agentService.getAllAgents();
    } catch (error) {
      console.error('Failed to load agents', error);
    }
  }
 
  addAgent(): void {
    // Role fixed as 'AGENT' for NEW agents
    this.selectedAgent = { agentid: '', name: '', email: '', phone: '', role: 'AGENT' };
    this.isEditing = true;
    this.isNew = true;
    this.formSubmitted = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.emailExists = false;
    this.agentIdExists = false;
  }
 
  editAgent(agent: Agent): void {
    // Clone the agent to avoid mutating the list directly
    this.selectedAgent = { ...agent };
    this.isEditing = true;
    this.isNew = false;
    this.formSubmitted = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.emailExists = false;
    this.agentIdExists = false;
  }
 
  async saveAgent(): Promise<void> {
    this.formSubmitted = true;
    this.errorMessage = '';
    this.successMessage = '';
 
    this.validateEmailUniqueness();
 
    // --- VALIDATION CHECKS (Kept as is) ---
    if (!this.selectedAgent.name?.trim()) {
      this.errorMessage = 'Name is required';
      return;
    }
 
    if (!this.isValidName(this.selectedAgent.name)) {
      this.errorMessage = 'Name must contain alphabets and spaces only';
      return;
    }
 
    if (!this.selectedAgent.email?.trim()) {
      this.errorMessage = 'Email is required';
      return;
    }
 
    if (!this.isValidEmail(this.selectedAgent.email)) {
      this.errorMessage = 'Please enter a valid email address';
      return;
    }
 
    if (this.emailExists) {
      this.errorMessage = 'Email already exists';
      return;
    }
 
    if (this.selectedAgent.phone?.trim() && !this.isValidPhone(this.selectedAgent.phone)) {
      this.errorMessage = 'Phone must be exactly 10 digits';
      return;
    }
 
    if (this.isNew) {
      if (!this.selectedAgent.password?.trim()) {
        this.errorMessage = 'Password is required for new agents';
        return;
      }
 
      if (!this.isValidPassword(this.selectedAgent.password)) {
        this.errorMessage = 'Password must be at least 8 characters with letters and numbers';
        return;
      }
    }
 
    try {
      let savedAgent: Agent;
     
      if (this.isNew) {
        console.log('📝 Creating new agent:', this.selectedAgent);
        savedAgent = await this.agentService.createAgent(this.selectedAgent);
       
        // This check relies on the backend returning the ID (whch we fixed in the Java code)
        if (!savedAgent || !savedAgent.agentid) {
          throw new Error('Server did not return a valid agent ID');
        }
       
        console.log('✅ Agent created with ID:', savedAgent.agentid);
        this.successMessage = `✓ Agent created successfully! ID: ${savedAgent.agentid}`;
        this.agents.push(savedAgent);
      } else {
        console.log('📝 Updating agent:', this.selectedAgent);
        const originalAgentId = this.selectedAgent.agentid;
        savedAgent = await this.agentService.updateAgent(this.selectedAgent);
       
        if (!savedAgent || !savedAgent.agentid) {
          throw new Error('Server did not return a valid agent');
        }
       
        console.log('✅ Agent updated:', savedAgent.agentid);
        this.successMessage = '✓ Agent updated successfully!';
       
        const index = this.agents.findIndex(a => a.agentid === originalAgentId);
        if (index !== -1) {
          this.agents[index] = savedAgent;
          this.agents = [...this.agents];
        }
      }
 
      setTimeout(() => this.successMessage = '', 4000);
      this.cancelEdit();
     
    } catch (error) {
      console.error('❌ Error details (Hidden from user):', error);
     
      // --- CHANGE IS HERE ---
      // Instead of showing error.message, we show a generic friendly message.
      this.errorMessage = '✗ Unable to save agent. Please try again.';
    }
  }
 
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test((email || '').trim());
  }
 
  private isValidName(name: string): boolean {
    const nameRegex = /^[A-Za-z . ]+$/;
    return nameRegex.test((name || '').trim());
  }
 
  private isValidPhone(phone: string): boolean {
    const phoneRegex = /^(\d{10})$/;
    return phoneRegex.test((phone || '').trim());
  }
 
  private isValidPassword(password: string): boolean {
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,}$/;
    return passwordRegex.test((password || '').trim());
  }
 
  private isValidAgentIdFormat(agentid: string): boolean {
    const idRegex = /^A-\d{3}$/;
    return idRegex.test((agentid || '').trim());
  }
 
  private isEmailUnique(email: string): boolean {
    const normalized = (email || '').trim().toLowerCase();
    if (!normalized) return true;
    // During edit, exclude current agent from check
    return !this.agents.some(agent =>
      (agent.email || '').toLowerCase() === normalized &&
      agent.agentid !== this.selectedAgent.agentid
    );
  }
 
  validateEmailUniqueness(): void {
    const email = (this.selectedAgent.email || '').trim();
    if (!email) {
      this.emailExists = false;
      return;
    }
    this.emailExists = !this.isEmailUnique(email);
  }
 
  async deleteAgent(agentid: string): Promise<void> {
    if (confirm('Are you sure you want to delete this agent?')) {
      try {
        await this.agentService.deleteAgent(agentid);
        console.log('Agent deleted successfully');
        // Remove from list without reload
        this.agents = this.agents.filter(a => a.agentid !== agentid);
        this.successMessage = 'Agent deleted successfully';
        setTimeout(() => this.successMessage = '', 3000);
      } catch (error) {
        // Keep generic error for delete too
        console.error('Failed to delete agent', error);
        this.errorMessage = 'Failed to delete agent.';
      }
    }
  }
 
  cancelEdit(): void {
    this.isEditing = false;
    this.isNew = false;
    this.formSubmitted = false;
    this.emailExists = false;
    this.agentIdExists = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.selectedAgent = { agentid: '', name: '', email: '', phone: '', role: '', password: '' };
  }
 
  // Optional: trackBy for better rendering performance
   trackByAgentId(index: number, agent: Agent): string {
    return agent.agentid ?? `${index}`;
  }
}