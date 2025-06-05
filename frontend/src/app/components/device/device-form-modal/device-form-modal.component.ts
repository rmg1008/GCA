import { Component, Input, Output, EventEmitter, ViewChild, ElementRef, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DeviceDTO } from '../../../models/device.dto';
import { CommonModule } from '@angular/common';
import { TreeNodeDTO } from '../../../models/tree-node.dto';
import { GroupService } from '../../../services/group/group.service';

@Component({
  standalone: true,
  selector: 'app-device-form-modal',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './device-form-modal.component.html',
})
export class DeviceFormModalComponent implements OnInit {
  @Input() deviceToEdit?: DeviceDTO; // Puede recibir el dispositivo a editar
  @Input() group?: TreeNodeDTO; // Recibe el grupo al que pertenece
  @Output() saved = new EventEmitter<DeviceDTO>(); // Evento para guardar el formulairo
  @Output() cancelled = new EventEmitter<void>(); // Evento para cancelar

  @ViewChild('modalRef') modalRef!: ElementRef<HTMLDialogElement>;

  form!: FormGroup;
  private groupService = inject(GroupService);
  groups: TreeNodeDTO[] = [];

  constructor(private fb: FormBuilder) {}

  ngOnInit(): void {}

  open(): void {

    // Carga el listado de grupos y selecciona al que pertenece
    this.groupService.getAllGroups().subscribe((groups) => {
      this.groups = groups;
      this.form.get('group')?.setValue(this.group?.id || '');
    });

    // Configuración inicial del formulario
    this.form = this.fb.group({
      id: [0],
      name: ['', Validators.required],
      fingerprint: ['', Validators.required],
      group: [this.group?.id, Validators.required],
      os: [1] 
    });
    
    this.form.reset(this.deviceToEdit || { id: 0, name: '', fingerprint: '', group: this.group?.id, os: 1 });
    this.modalRef.nativeElement.showModal();
  }

  close(): void {
    this.modalRef.nativeElement.close();
    this.cancelled.emit();
  }

  save(): void {
    if (this.form.valid) {
      this.saved.emit(this.form.value); // Lanza el 'aviso' al padre con los nuevos valores
      this.close();
    }
  }
}

