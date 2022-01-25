import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EscalationReportComponent } from './escalationreport.component';

describe('EscalationReportComponent', () => {
  let component: EscalationReportComponent;
  let fixture: ComponentFixture<EscalationReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EscalationReportComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EscalationReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
