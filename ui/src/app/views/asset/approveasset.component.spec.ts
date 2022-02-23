import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActionAssetRequestComponent, } from './approveasset.component';

describe('ActionAssetRequestComponent', () => {
  let component: ActionAssetRequestComponent;
  let fixture: ComponentFixture<ActionAssetRequestComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActionAssetRequestComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActionAssetRequestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
