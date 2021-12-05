import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RaiseIncidentComponent, } from './raiseincident.component';

describe('RaiseIncidentComponent', () => {
  let component: RaiseIncidentComponent;
  let fixture: ComponentFixture<RaiseIncidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RaiseIncidentComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RaiseIncidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
