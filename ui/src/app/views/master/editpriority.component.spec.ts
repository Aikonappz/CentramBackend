import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditPriorityComponent } from './editpriority.component';

describe('EditPriorityComponent', () => {
  let component: EditPriorityComponent;
  let fixture: ComponentFixture<EditPriorityComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditPriorityComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditPriorityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
