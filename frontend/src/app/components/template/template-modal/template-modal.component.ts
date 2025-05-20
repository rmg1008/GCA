import { CommonModule } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, Output, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TemplateDTO } from '../../../models/template.dto';

@Component({
  selector: 'app-template-modal',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './template-modal.component.html',
  styleUrl: './template-modal.component.css'
})
export class TemplateModalComponent implements OnChanges {
  @Input() templateToEdit?: TemplateDTO;
  @Output() saved = new EventEmitter<TemplateDTO>();
  @ViewChild('modalRef') modalRef!: ElementRef<HTMLDialogElement>;

  form!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['templateToEdit'] && this.form) {
      this.form.reset(this.templateToEdit || { id: 0, name: '', description: '', os: 1 });
    }
  }

  open(): void {
    if (!this.form) {
      this.form = this.fb.group({
        id: [0],
        name: ['', Validators.required],
        description: [''],
        os: [1],
      });
    }

    this.form.reset(this.templateToEdit || { id: 0, name: '', description: '', os: 1 });
    this.modalRef.nativeElement.showModal();
  }

  close(): void {
    this.modalRef.nativeElement.close();
  }

  save(): void {
    if (this.form.valid) {
      this.saved.emit(this.form.value);
      this.close();
    }
  }

    isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }
}
