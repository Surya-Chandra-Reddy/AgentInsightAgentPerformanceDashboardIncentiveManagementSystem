export interface Incentive {
  incentiveid: string;
  agentid: string;
  amount: number;
  calculationdate: string; // mm/dd/yyyy format
  status: 'Pending' | 'Paid' | string;
  agentName?: string;
  bonus?: number;
}
