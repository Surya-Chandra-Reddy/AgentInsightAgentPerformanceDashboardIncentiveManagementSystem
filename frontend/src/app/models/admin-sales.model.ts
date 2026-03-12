export interface Sale {
  id: string;
  policyId: string;
  policyName?: string;
  agent: string;
  amount: number;
  date: string; // ISO date string e.g. "2024-04-20"
  status?: 'pending' | 'verified' | 'rejected';
}