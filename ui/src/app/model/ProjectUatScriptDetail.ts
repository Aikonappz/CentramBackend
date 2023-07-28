import { Base } from "./Base"

export interface ProjectUatScriptDetail extends Base {
    id: number
    testScenarioJobId: string
    step: number
    action: string
    expectedResult: string
    actualResult: any
    pass: boolean
    retestDate: any
    retestPass: boolean
    remarks: any
  }