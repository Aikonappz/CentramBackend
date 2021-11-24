import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HolidayCalenderComponent } from './holidaycalender.component';

describe('HolidayCalenderComponent', () => {
  let component: HolidayCalenderComponent;
  let fixture: ComponentFixture<HolidayCalenderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HolidayCalenderComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HolidayCalenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
