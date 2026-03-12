import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentIncentives } from './agent-incentives';

describe('AgentIncentives', () => {
  let component: AgentIncentives;
  let fixture: ComponentFixture<AgentIncentives>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentIncentives]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgentIncentives);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
