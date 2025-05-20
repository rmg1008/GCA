export interface TemplateCommandDTO {
    commandId: number;
    commandName?: string;
    commandDescription?: string;
    commandValue?: string;
    executionOrder: number;
    parameterValues?: { [key: string]: string };
  }
  