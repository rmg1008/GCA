export interface TreeNodeDTO {
    id: number;
    name: string;
    children?: TreeNodeDTO[];
    parentId: number;
    templateId: number;
  }