import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserIncidentComponent } from './userincident.component';

describe('UserIncidentComponent', () => {
  let component: UserIncidentComponent;
  let fixture: ComponentFixture<UserIncidentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UserIncidentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UserIncidentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
