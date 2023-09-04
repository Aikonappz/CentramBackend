import { Base } from "./Base"

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
}
export interface ProjectUatScriptDetailList {
  content: ProjectUatScriptDetail[];
  totalElements: number;
}