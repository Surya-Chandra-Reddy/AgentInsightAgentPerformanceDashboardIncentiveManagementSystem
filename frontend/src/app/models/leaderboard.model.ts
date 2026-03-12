export interface Leaderboard {
  id?: number;
  entryId: string;
  agentId: string;
  rank: number;
  totalSales: number;
  agentName?: string; 
  lastUpdated?: Date;
}