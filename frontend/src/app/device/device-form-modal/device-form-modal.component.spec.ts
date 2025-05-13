import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeviceFormModalComponent } from './device-form-modal.component';

describe('DeviceFormModalComponent', () => {
  let component: DeviceFormModalComponent;
  let fixture: ComponentFixture<DeviceFormModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeviceFormModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeviceFormModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
