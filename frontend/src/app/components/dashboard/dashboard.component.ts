import { Component, ElementRef, HostListener, inject, OnInit, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TreeNodeComponent } from '../tree-node/tree-node.component';
import { TreeService } from '../../services/tree/tree.service';
import { TreeNodeDTO } from '../../models/tree-node.dto';
import { DeviceService } from '../../services/device/device.service';
import { DeviceDTO } from '../../models/device.dto';
import { DeviceComponent } from '../../device/device.component';
import { GroupService } from '../../services/group/group.service';
import { GroupDTO } from '../../models/group.dto';
import { ToastService } from '../../services/toast/toast.service';
import { AssignGroupToTemplateComponent } from '../groups/assign-group-to-template/assign-group-to-template.component';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, TreeNodeComponent, DeviceComponent, FormsModule, AssignGroupToTemplateComponent],
  templateUrl: './dashboard.component.html',
})
export class DashboardComponent implements OnInit {
  @ViewChild('groupModal') groupModalRef!: ElementRef<HTMLDialogElement>;
  private treeService = inject(TreeService);
  private deviceService = inject(DeviceService);
  private groupService = inject(GroupService);
  private toastService = inject(ToastService);

  selectedTab: 'dispositivos' | 'plantillas' = 'dispositivos';
  treeData?: TreeNodeDTO;
  currentGroup!: TreeNodeDTO;
  selectedPath: TreeNodeDTO[] = [];
  selectedGroupDevices: DeviceDTO[] = [];
  contextMenuGroupId: number | null = null;
  contextMenuPosition = { x: 0, y: 0 };
  openedNodeIds: number[] = [];
  groupNameInput: string = '';
  isEditingGroup = false;
  groupToEdit: TreeNodeDTO | null = null;

  ngOnInit(): void {
    this.loadTree();
  }

  loadTree(): void {
    this.saveOpenedNodes();
    this.treeService.getTree().subscribe({
      next: (data) => {
        this.treeData = data;
        this.restoreOpenedNodes();
      },
      error: (err) => console.error('Error cargando árbol:', err),
    });
  }


  selectGroup(group: TreeNodeDTO): void {
    this.currentGroup = group;
    this.selectedPath = this.findPathToGroup(this.treeData!, group.id);
      this.deviceService.getDevicesByGroup(group.id).subscribe({
      next: (data) => (this.selectedGroupDevices = data),
      error: (err) => console.error('Error cargando dispositivos:', err),
    });
    console.log(this.selectedPath)
  }

  private findPathToGroup(node: TreeNodeDTO, targetId: number): TreeNodeDTO[] {
    if (node.id === targetId) {
      return [node];
    }

    for (const child of node.children || []) {
      const path = this.findPathToGroup(child, targetId);
      if (path.length) {
        return [node, ...path];
      }
    }

    return [];
  }

  onContextMenu({ node, x, y }: { node: TreeNodeDTO; x: number; y: number }): void {
    this.contextMenuGroupId = node.id;
    this.contextMenuPosition = { x, y };
    this.groupToEdit = node;
  }

  closeContextMenu(): void {
    this.contextMenuGroupId = null;
  }


  addGroup(parentId?: number): void {
  this.isEditingGroup = false;
  this.contextMenuGroupId = this.contextMenuGroupId;
  this.groupNameInput = '';
  this.groupToEdit = null;
  (this.groupModalRef.nativeElement as HTMLDialogElement).showModal();
}

  editGroup(): void {
  if (!this.contextMenuGroupId || !this.groupToEdit) return;
    this.isEditingGroup = true;
    this.groupNameInput = this.groupToEdit.name;
    (this.groupModalRef.nativeElement as HTMLDialogElement).showModal();
  }

    confirmGroup(): void {
    const name = this.groupNameInput.trim();
    if (this.contextMenuGroupId !== null) {
      if (this.isEditingGroup && this.groupToEdit) {
        const updated: GroupDTO = {
          id: this.groupToEdit.id,
          name: name,
          parent: this.groupToEdit.parentId
        };
        this.groupService.updateGroup(updated).subscribe(() => {
          this.toastService.show('success', 'Grupo actualizado');
          this.loadTree();
          (this.groupModalRef.nativeElement as HTMLDialogElement).close();
        });
      } else {
        const parentId = this.contextMenuGroupId;
        console.log('Añadir grupo bajo el nodo con ID:', parentId);
        const newGroup: GroupDTO = {
          id: null as any,
          name: name,
          parent: this.contextMenuGroupId
        };
        this.groupService.addGroup(newGroup).subscribe({
          next: () => {
            this.toastService.show("success", "Grupo añadido")
            this.loadTree();
          },
          error: (err) => {
            this.toastService.show("error", "Error al añadir el grupo")

          }
        });
      }
      this.closeGroupModal();
      this.closeContextMenu();
  }
}

  deleteGroup(): void {
    if (this.contextMenuGroupId !== null) {
      const groupIdToDelete = this.contextMenuGroupId;
      console.log('Eliminar grupo con ID:', groupIdToDelete);

      const parentNode = this.findParentNode(this.treeData!, groupIdToDelete);

      this.groupService.deleteGroup(groupIdToDelete).subscribe({
        next: () => {
          this.toastService.show("success", "Grupo eliminado")
          this.loadTree();

          setTimeout(() => {
            if (parentNode) {
              this.selectGroup(parentNode);
            }
          }, 0);

        },
        error: (err) => {
          this.toastService.show("error", "Error al eliminar el grupo")
        }
      });
      this.closeContextMenu();
    }
  }


  closeGroupModal(): void {
  (this.groupModalRef.nativeElement as HTMLDialogElement).close();
}

  findParentNode(current: TreeNodeDTO, childId: number): TreeNodeDTO | null {
    if (!current.children) return null;

    for (const child of current.children) {
      if (child.id === childId) {
        return current;
      }

      const found = this.findParentNode(child, childId);
      if (found) return found;
    }

    return null;
  }

  saveOpenedNodes(): void {
    this.openedNodeIds = this.selectedPath.map(node => node.id);
  }

  restoreOpenedNodes(): void {
    if (!this.treeData || this.openedNodeIds.length === 0) return;

    const path: TreeNodeDTO[] = [];
    let currentLevel = this.treeData;

    for (const id of this.openedNodeIds) {
      const match = this.findNodeById(currentLevel, id);
      if (match) {
        path.push(match);
        currentLevel = match.children?.[0] ?? match;
      }
    }

    this.selectedPath = path;
  }

  findNodeById(node: TreeNodeDTO, id: number): TreeNodeDTO | null {
    if (node.id === id) return node;

    for (const child of node.children || []) {
      const found = this.findNodeById(child, id);
      if (found) return found;
    }

    return null;
  }


  @HostListener('document:click', ['$event'])
  onClickOutside(event: MouseEvent): void {
    const modalOpen = (this.groupModalRef?.nativeElement as HTMLDialogElement)?.open;
    if (!modalOpen) {
      this.closeContextMenu();
    }
  }

}
