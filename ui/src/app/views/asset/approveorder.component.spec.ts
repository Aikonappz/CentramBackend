import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ApproveOrderComponent } from './approveorder.component';

describe('ApproveOrderComponent', () => {
  let component: ApproveOrderComponent;
  let fixture: ComponentFixture<ApproveOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApproveOrderComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ApproveOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
