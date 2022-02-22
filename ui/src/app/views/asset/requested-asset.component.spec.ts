import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RequestedAssetComponent } from './requested-asset.component';

describe('RequestedAssetComponent', () => {
  let component: RequestedAssetComponent;
  let fixture: ComponentFixture<RequestedAssetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RequestedAssetComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestedAssetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
