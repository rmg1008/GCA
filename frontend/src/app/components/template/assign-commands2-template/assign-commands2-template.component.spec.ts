import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignCommands2TemplateComponent } from './assign-commands2-template.component';

describe('AssignCommands2TemplateComponent', () => {
  let component: AssignCommands2TemplateComponent;
  let fixture: ComponentFixture<AssignCommands2TemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignCommands2TemplateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignCommands2TemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
