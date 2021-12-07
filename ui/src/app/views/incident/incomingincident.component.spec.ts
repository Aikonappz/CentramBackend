import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IncomingIncidentComponent } from './incomingincident.component';

describe('IncomingIncidentComponent', () => {
  let component: IncomingIncidentComponent;
  let fixture: ComponentFixture<IncomingIncidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IncomingIncidentComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IncomingIncidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
