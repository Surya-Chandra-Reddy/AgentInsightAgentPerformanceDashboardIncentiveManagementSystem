import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentSales } from './agent-sales';

describe('AgentSales', () => {
  let component: AgentSales;
  let fixture: ComponentFixture<AgentSales>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgentSales]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgentSales);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
