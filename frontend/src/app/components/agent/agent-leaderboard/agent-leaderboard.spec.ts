import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AgentLeaderboard } from './agent-leaderboard';

describe('AgentLeaderboard', () => {
  let component: AgentLeaderboard;
  let fixture: ComponentFixture<AgentLeaderboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentLeaderboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgentLeaderboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
