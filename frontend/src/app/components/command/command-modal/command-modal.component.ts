import { CommonModule } from '@angular/common';
import { Component, ElementRef, EventEmitter, Input, OnChanges, Output, SimpleChanges, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { CommandDTO } from '../../../models/command.dto';

@Component({
  selector: 'app-command-modal',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './command-modal.component.html',
  styleUrl: './command-modal.component.css'
})
export class CommandModalComponent implements OnChanges {
  @Input() commandToEdit?: CommandDTO;
  @Output() saved = new EventEmitter<CommandDTO>();
  @ViewChild('modalRef') modalRef!: ElementRef<HTMLDialogElement>;

  form!: FormGroup;

  constructor(private fb: FormBuilder) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['commandToEdit'] && this.form) {
      this.form.reset(this.commandToEdit || { id: 0, name: '', description: '', value: '' });
    }
  }

  open(): void {
      this.form = this.fb.group({
        id: [0],
        name: ['', Validators.required],
        description: [''],
        value: ['', [Validators.required, this.commandValueValidator()]],
      });

    this.form.reset(this.commandToEdit || { id: 0, name: '', description: '', value: '' });
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

  /* Se validan que las variables tengan la estructura {{nombre}} */
  public commandValueValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const value: string = control.value;
      if (!value) return null;

      const regex = /{{.*?}}/g;
      const allMatches = value.match(regex) || [];

      const malformed = allMatches.filter(variable => {
        const inner = variable.replace(/{{\s*|\s*}}/g, '');
        return !/^\w+$/.test(inner);
      });

      const invalidOpen = value.includes('{{') && !value.includes('}}');
      const invalidClose = value.includes('}}') && !value.includes('{{');
      const unmatchedPairs = (value.match(/{{/g)?.length || 0) !== (value.match(/}}/g)?.length || 0);

      if (malformed.length > 0 || invalidOpen || invalidClose || unmatchedPairs) {
        return {
          invalidVariableFormat: true
        };
      }
      return null;
    };
  }

  isInvalid(controlName: string): boolean {
    const control = this.form.get(controlName);
    return !!control && control.invalid && (control.touched || control.dirty);
  }

  getUsedVariables(): string[] {
    const value = this.form.get('value')?.value || '';
    const matches: string[] | null = value.match(/{{\s*(\w+)\s*}}/g);
    return matches?.map(v => v.replace(/{{\s*|\s*}}/g, '')) || [];
  }
}