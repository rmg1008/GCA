export interface CommandDTO {
    id: number;
    name: string;
    description: string;
    value: string;
    placeholders?: string[];
  }