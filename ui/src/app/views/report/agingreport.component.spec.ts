import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AgingReportComponent } from './agingreport.component';

describe('AgingReportComponent', () => {
  let component: AgingReportComponent;
  let fixture: ComponentFixture<AgingReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AgingReportComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgingReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
