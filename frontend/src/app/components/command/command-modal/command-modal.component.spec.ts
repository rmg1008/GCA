import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommandModalComponent } from './command-modal.component';

describe('CommandModalComponent', () => {
  let component: CommandModalComponent;
  let fixture: ComponentFixture<CommandModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommandModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CommandModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
