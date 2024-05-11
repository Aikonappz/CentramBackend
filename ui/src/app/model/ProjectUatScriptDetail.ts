import { Base } from "./Base"
import { User } from "./User";

export class ProjectUatScriptDetail extends Base {
  id: number;
  testScenarioJobId: string;
  step: number;
  action: string;
  expectedResult: string;
  actualResult: string;
  pass: boolean;
  retestDate: string;
  retestPass: boolean;
  remarks: any[] = [];
  remark: string;
  editable: boolean = false;
  previousStepPassed: boolean = false;
  customerUser: User;
  saved: boolean = false;
  attachments: any[] = [];
  attahmentUploaded: boolean = false;
}
export interface ProjectUatScriptDetailList {
  content: ProjectUatScriptDetail[];
  totalElements: number;
}