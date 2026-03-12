import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Sidebar } from '../../sidebar/sidebar';
import { SalesService } from '../../../services/sales.service';
import { Sale } from '../../../models/sale.model';
import { Agent } from '../../../models/agent.model';
import { AuthService } from '../../../services/auth.service';
import { ToastService } from '../../../services/toast.service';
import { PolicyService, PolicyDto } from '../../../services/policy.service';

interface SalesWithDetails extends Sale {
  agentName?: string;
  policyName?: string;
}

interface Policy {
  policyid: string;
  name: string;
}

@Component({
  selector: 'app-agent-sales',
  standalone: true,
  imports: [CommonModule, FormsModule, Sidebar],
  templateUrl: './agent-sales.html',
  styleUrl: './agent-sales.css'
})
export class AgentSales implements OnInit {
  policies: Policy[] = [];
  sales: SalesWithDetails[] = [];
  agents: Agent[] = [];

  totalsales = 0;
  conversionRate = 0;

  // Basic pagination
  page = 1;
  pageSize = 6;

  loading = true;

  currentAgentId = '';

  // Modal state
  showModal = signal<boolean>(false);
  isEditMode = signal<boolean>(false);
  formSubmitting = signal<boolean>(false);
  editDisabledReason = signal<string>('');

  // New: policy modal state
  showPolicyModal = signal<boolean>(false);
  policyFormSubmitting = signal<boolean>(false);

  // New: show current policies inside modal
  showPolicyList = signal<boolean>(false);
  policyListLoading = false;
  // Use a small display DTO for the modal list
  policyList: { displayPolicyId: string; name: string }[] = [];

  // New: policy form model (removed policyId input, keep policyCode for user input)
  policyForm: { policyCode: string; name: string } = { policyCode: '', name: '' };

  originalSale: SalesWithDetails | null = null;

  // NOTE: changed `amount` -> `saleamount` to match backend DTO
  formData!: {
    saleid?: string;
    policyid: string;
    agentid: string;
    amount: number;
    saletype: string;
    saleDate: string;
  };

  constructor(
    private salesService: SalesService,
    private authService: AuthService,
    private toast: ToastService,
    private policyService: PolicyService
  ) {}

  async ngOnInit(): Promise<void> {
    try {
      const user = this.authService.currentUser();
      if (user) this.currentAgentId = user.agentid;
      await this.loadSalesData();
    } catch (err) {
      console.error('ngOnInit error', err);
      this.toast.show('Failed to initialize', 'error');
    }
  }

  private async loadSalesData(): Promise<void> {
    try {
      this.loading = true;

      const token = this.authService.getToken();
      if (!token) {
        this.toast.show('Not authenticated — please log in.', 'error');
        this.loading = false;
        return;
      }

      // Load policies - use policyId (POL-001) as the value
      const remotePolicies = await this.policyService.getPolicies();
      this.policies = (remotePolicies || []).map(p => ({
        policyid: (p.policyId || p.policyid || p.policy_id || p.policyCode || '') as string,
        name: p.name || ''
      }));

      // Load agents
      this.agents = await this.salesService.getAgents();

      // Load sales with details for current agent only
      const allSales = await this.salesService.getAllSalesWithDetails();
      this.sales = allSales.filter(sale => sale.agentid === this.currentAgentId);

      // Calculate aggregates for current agent
      this.totalsales = this.sales.reduce((sum, sale) => sum + (sale.amount || 0), 0);
      this.conversionRate = await this.salesService.getConversionRate();
      // ensure current page still valid
      if ((this.page - 1) * this.pageSize >= this.sales.length) {
        this.page = 1;
      }
    } catch (error) {
      console.error('Error loading sales data:', error);
      this.toast.show('Failed to load sales', 'error');
    } finally {
      this.loading = false;
    }
  }

  // ✅ Check if sale can be edited
  canEditSale(sale: SalesWithDetails): boolean {
    const status = sale.status?.toLowerCase() || '';
    return !['completed', 'cancelled', 'verified'].includes(status);
  }

  // ✅ Get reason why sale cannot be edited
  getEditDisabledReason(sale: SalesWithDetails): string {
    const status = sale.status || 'Unknown';
    if (['Completed', 'Cancelled', 'Verified'].includes(status)) {
      return `Edit is disabled for ${status} sales.`;
    }
    return '';
  }

  // ✅ Open Add Sale Modal
  openAddModal(): void {
    this.editDisabledReason.set('');
    this.isEditMode.set(false);
    this.formData = {
      policyid: this.policies[0]?.policyid || '',
      agentid: this.currentAgentId,
      amount: 0,
      saletype: 'New',
      saleDate: new Date().toISOString().split('T')[0]
    };
    this.showModal.set(true);
  }

  // ✅ Open Edit Modal
  openEditModal(sale: SalesWithDetails): void {
    if (!this.canEditSale(sale)) {
      this.editDisabledReason.set(this.getEditDisabledReason(sale));
      this.isEditMode.set(true);
      this.showModal.set(true);
      return;
    }
    this.editDisabledReason.set('');
    this.isEditMode.set(true);
    this.originalSale = sale;
    this.formData = {
      saleid: sale.saleid,
      policyid: sale.policyid || '',
      agentid: sale.agentid || '',
      amount: sale.amount ?? 0,
      saletype: sale.saletype ?? 'New',
      saleDate: sale.saleDate || new Date().toISOString().split('T')[0]
    };
    this.showModal.set(true);
  }

  // ✅ Close Modal
  closeModal(): void {
    this.showModal.set(false);
    this.formSubmitting.set(false);
    this.editDisabledReason.set('');
    this.resetForm();
  }

  // ✅ Reset Form
  private resetForm(): void {
    this.formData = {
      policyid: this.policies[0]?.policyid || '',
      agentid: this.currentAgentId,
      amount: 0,
      saletype: 'New',
      saleDate: new Date().toISOString().split('T')[0]
    };
  }

  // ✅ Submit Form (Add or Update)
  async submitForm(): Promise<void> {
    // Validation
    if (!this.formData.policyid || this.formData.amount == null || !this.formData.saleDate) {
      this.toast.show('Please fill all required fields', 'error');
      return;
    }

    this.formSubmitting.set(true);

    try {
      // ensure ISO date (backend expects LocalDateTime ISO)
      const isoDate = new Date(this.formData.saleDate).toISOString();

      if (this.isEditMode()) {
        // Update existing sale
        if (!this.formData.saleid) {
          throw new Error('Sale ID is missing');
        }
        const saleToUpdate= {
          saleid: this.formData.saleid,
          policyid: this.formData.policyid,
          agentid: this.formData.agentid,
          saleamount: this.formData.amount,
          saletype: this.formData.saletype,
          saledate: isoDate,
          status: this.originalSale?.status || 'Pending'
        };
        await this.salesService.updateSale(saleToUpdate.saleid!, saleToUpdate);
        this.toast.show('Sale updated successfully', 'success');
      } else {
        // Create new sale — include saleid as backend example shows
        const newSaleId = this.generateSaleId();
        const salePayload = {
          saleid: newSaleId,
          policyid: this.formData.policyid,
          agentid: this.formData.agentid,
          saleamount: this.formData.amount,
          saletype: this.formData.saletype,
          saledate: isoDate,
          status: 'Pending'
        };
        await this.salesService.createSale(salePayload);
        this.toast.show('Sale added successfully', 'success');
      }

      // Reload data and close modal
      await this.loadSalesData();
      this.closeModal();
    } catch (err) {
      console.error('Form submission error', err);
      this.toast.show(
        this.isEditMode() ? 'Failed to update sale' : 'Failed to add sale',
        'error'
      );
    } finally {
      this.formSubmitting.set(false);
    }
  }

  // New: Open Add Policy Modal
  openAddPolicyModal(): void {
    this.policyForm = { policyCode: '', name: '' };
    this.policyFormSubmitting.set(false);
    this.showPolicyList.set(false);
    this.policyList = [];
    this.showPolicyModal.set(true);
  }

  // Fixed: Close Policy Modal implementation
  closePolicyModal(): void {
    this.showPolicyModal.set(false);
    this.policyFormSubmitting.set(false);
    this.showPolicyList.set(false);
    this.policyList = [];
    this.policyForm = { policyCode: '', name: '' };
  }

  // New: Toggle and load current policies for modal
  async togglePolicyList(): Promise<void> {
    const willShow = !this.showPolicyList();
    this.showPolicyList.set(willShow);
    if (!willShow) {
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      this.toast.show('Not authenticated — please log in to view policies.', 'error');
      this.showPolicyList.set(false);
      return;
    }

    this.policyListLoading = true;
    try {
      const policies = await this.policyService.getPolicies();
      this.policyList = (policies || []).map((p: PolicyDto) => {
        const displayId = p.policyId || p.policyCode || p.policy_id || p.policyid || '';
        return { displayPolicyId: displayId, name: p.name || '' };
      });
    } catch (err) {
      console.error('Failed to load current policies', err);
      this.toast.show('Failed to load current policies', 'error');
      this.policyList = [];
    } finally {
      this.policyListLoading = false;
    }
  }

  // New: Submit Policy Form with auto-generated policyId
  async submitPolicyForm(): Promise<void> {
    if (!this.policyForm.policyCode || !this.policyForm.name) {
      this.toast.show('Please fill all policy fields (policy code, name)', 'error');
      return;
    }

    const token = this.authService.getToken();
    if (!token) {
      this.toast.show('Not authenticated — please log in to add policies.', 'error');
      return;
    }

    this.policyFormSubmitting.set(true);
    try {
      const payload: PolicyDto = {
        policyCode: this.policyForm.policyCode,
        name: this.policyForm.name
      };
      await this.policyService.createPolicy(payload);
      this.toast.show('Policy added', 'success');

      // Refresh policies - use policyId as value
      try {
        const remotePolicies = await this.policyService.getPolicies();
        this.policies = (remotePolicies || []).map(p => ({
          policyid: (p.policyId || p.policyid || p.policy_id || p.policyCode || '') as string,
          name: p.name || ''
        }));
      } catch (e) {
        console.warn('Failed to refresh policy list after create', e);
      }

      this.closePolicyModal();
    } catch (err) {
      console.error('Failed to add policy', err);
      this.toast.show('Failed to add policy', 'error');
    } finally {
      this.policyFormSubmitting.set(false);
    }
  }

  async deleteSale(s: SalesWithDetails): Promise<void> {
    if (!confirm(`Delete sale ${s.saleid}?`)) return;
    try {
      this.loading = true;
      await this.salesService.deleteSale(s.saleid || '');
      await this.loadSalesData();
      this.toast.show(`Sale ${s.saleid} deleted`, 'success');
    } catch (err) {
      console.error('Failed to delete sale', err);
      this.toast.show('Failed to delete sale', 'error');
    } finally {
      this.loading = false;
    }
  }

  getPolicyName(policyid: string): string {
    const p = this.policies.find((x) => x.policyid === policyid);
    return p ? p.name : policyid;
  }

  getAgentName(agentid: string): string {
    const a = this.agents.find((x) => x.agentid === agentid);
    return a ? a.name : agentid;
  }

  // pagination helpers
  get pagedSales(): SalesWithDetails[] {
    const start = (this.page - 1) * this.pageSize;
    return this.sales.slice(start, start + this.pageSize);
  }

  prevPage(): void {
    if (this.page > 1) this.page--;
  }
  nextPage(): void {
    if (this.page * this.pageSize < this.sales.length) this.page++;
  }

  // utility: generate sale id (S + timestamp)
  private generateSaleId(): string {
    return 'S' + Date.now().toString(36).toUpperCase();
  }

  // utility: format ISO date to readable string (e.g. "Jan 10, 2026")
  formatDate(isoDate: string): string {
    try {
      const date = new Date(isoDate);
      return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    } catch {
      return isoDate; // fallback to original if parse fails
    }
  }
}
