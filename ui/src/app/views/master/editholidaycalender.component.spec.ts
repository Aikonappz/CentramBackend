import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditHolidayCalenderComponent } from './editholidaycalender.component';

describe('EditHolidayCalenderComponent', () => {
  let component: EditHolidayCalenderComponent;
  let fixture: ComponentFixture<EditHolidayCalenderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditHolidayCalenderComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditHolidayCalenderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
