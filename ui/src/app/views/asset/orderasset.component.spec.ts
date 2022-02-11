import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderAssetComponent } from './orderasset.component';

describe('OrderAsset', () => {
  let component: OrderAssetComponent;
  let fixture: ComponentFixture<OrderAssetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrderAssetComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderAssetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
