export interface Sale {
  saleid: string;
  policyid: string;
  agentid: string;
  amount: number;
  saletype?: string;
  saleDate: string; // ISO or mm/dd/yyyy string
  status?: 'Completed' | 'Pending' | 'Cancelled' | string; // new status field
  agentName?: string;
  policyName?: string;
}
