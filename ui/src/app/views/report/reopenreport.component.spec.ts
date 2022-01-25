import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReopenReportComponent } from './reopenreport.component';

describe('ReopenReportComponent', () => {
  let component: ReopenReportComponent;
  let fixture: ComponentFixture<ReopenReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReopenReportComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReopenReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
