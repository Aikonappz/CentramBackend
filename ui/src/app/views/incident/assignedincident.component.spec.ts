import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AssignedIncidentComponent } from './assignedincident.component';

describe('AssignedIncidentComponent', () => {
  let component: AssignedIncidentComponent;
  let fixture: ComponentFixture<AssignedIncidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssignedIncidentComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssignedIncidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
