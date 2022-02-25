import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AssetRequestActionComponent, } from './action-asset.component';

describe('ActionAssetRequestComponent', () => {
  let component: AssetRequestActionComponent;
  let fixture: ComponentFixture<AssetRequestActionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AssetRequestActionComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssetRequestActionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
