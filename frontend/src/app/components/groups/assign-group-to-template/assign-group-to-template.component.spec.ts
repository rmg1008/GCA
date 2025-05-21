import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssignGroupToTemplateComponent } from './assign-group-to-template.component';

describe('AssignGroupToTemplateComponent', () => {
  let component: AssignGroupToTemplateComponent;
  let fixture: ComponentFixture<AssignGroupToTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AssignGroupToTemplateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AssignGroupToTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
