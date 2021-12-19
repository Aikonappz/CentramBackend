import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AgentIncidentComponent } from './agentincident.component';

describe('AgentIncidentComponent', () => {
  let component: AgentIncidentComponent;
  let fixture: ComponentFixture<AgentIncidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AgentIncidentComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AgentIncidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
