import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EditDlComponent } from './editdl.component';

describe('EditDlComponent', () => {
  let component: EditDlComponent;
  let fixture: ComponentFixture<EditDlComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditDlComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditDlComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
