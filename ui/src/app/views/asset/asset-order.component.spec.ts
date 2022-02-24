import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AssetOrderComponent, } from './asset-order.component';

describe('AddOrderComponent', () => {
  let component: AssetOrderComponent;
  let fixture: ComponentFixture<AssetOrderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssetOrderComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssetOrderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
