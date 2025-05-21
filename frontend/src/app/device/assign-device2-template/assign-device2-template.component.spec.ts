import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignDevice2TemplateComponent } from './assign-device2-template.component';

describe('AssignDevice2TemplateComponent', () => {
  let component: AssignDevice2TemplateComponent;
  let fixture: ComponentFixture<AssignDevice2TemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignDevice2TemplateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignDevice2TemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
