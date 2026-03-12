import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminIncentives } from './admin-incentives';

describe('AdminIncentives', () => {
  let component: AdminIncentives;
  let fixture: ComponentFixture<AdminIncentives>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminIncentives]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminIncentives);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
