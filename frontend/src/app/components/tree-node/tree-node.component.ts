import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TreeNodeDTO } from '../../models/tree-node.dto';

/**
 * Componente para gestionar la estructura arbórea
 */
@Component({
  selector: 'app-tree-node',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tree-node.component.html',
})
export class TreeNodeComponent {
  @Input() node!: TreeNodeDTO;
  @Input() selectedPath: TreeNodeDTO[] = [];
  @Input() contextMenuGroupId!: number | null;
  @Input() contextMenuPosition!: { x: number; y: number };
  @Output() groupSelected = new EventEmitter<TreeNodeDTO>();
  @Output() contextMenuOpened = new EventEmitter<{ node: TreeNodeDTO; x: number; y: number }>();
  @Output() contextMenuOpenedFromChild = new EventEmitter<{ node: TreeNodeDTO; x: number; y: number }>();
  @Output() contextMenuClosed = new EventEmitter<void>();

  /**
   * Al seleccionar un nuevo grupo, se envía un evento a los demás componentes
   * para indicar que el grupo ha cambiado y poder actualizar plantillas, dispositivos...
   */
  onGroupClick(): void {
    this.groupSelected.emit(this.node);
  }

  /**
   * Muestra el menú de gestión del grupo
   * al hacer clic derecho sobre este
   * @param event Evento de clic
   */
  onRightClick(event: MouseEvent): void {
    event.preventDefault();
    event.stopPropagation();
  
    const payload = {
      node: this.node,
      x: event.clientX,
      y: event.clientY
    }
    this.contextMenuOpened.emit(payload);
    this.contextMenuOpenedFromChild.emit(payload);
  }
  

  isContextMenuOpen(): boolean {
    return this.node.id === this.contextMenuGroupId;
  }

  isSelected(): boolean {
    return this.selectedPath?.[this.selectedPath.length - 1] === this.node;
  }

  isExpanded(): boolean {
    return this.selectedPath?.includes(this.node);
  }
}
