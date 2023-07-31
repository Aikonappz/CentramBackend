import { Base } from "./Base"

export class ProjectUatScriptDetail extends Base {
  id: number;
  testScenarioJobId: string;
  step: number;
  action: string;
  expectedResult: string;
  actualResult: any;
  pass: boolean;
  retestDate: any;
  retestPass: boolean;
  remarks: any;
  editable: boolean = false;
}
export interface ProjectUatScriptDetailList {
  content: ProjectUatScriptDetail[];
  totalElements: number;
}