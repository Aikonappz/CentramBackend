import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IncommingRequestedAssetComponent } from './incomming-requested-asset.component';

describe('IncommingRequestedAssetComponent', () => {
  let component: IncommingRequestedAssetComponent;
  let fixture: ComponentFixture<IncommingRequestedAssetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IncommingRequestedAssetComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IncommingRequestedAssetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
