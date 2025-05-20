import { CommandDTO } from "./command.dto";

export interface AssignedCommandsDTO {
  command: CommandDTO;
  order: number;
  parameterValues?: { [key: string]: string };
}
