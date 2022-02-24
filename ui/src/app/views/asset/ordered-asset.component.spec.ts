import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrderedAssetComponent } from './ordered-asset.component';

describe('OrderedAssetComponent', () => {
  let component: OrderedAssetComponent;
  let fixture: ComponentFixture<OrderedAssetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrderedAssetComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrderedAssetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
