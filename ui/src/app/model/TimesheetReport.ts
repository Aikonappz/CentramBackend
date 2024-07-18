import { Base } from "./Base"
import { MediaFile } from "./MediaFile"
import { Organisation } from "./Organisation"
import { Project } from "./Project"
import { ProjectUatScript } from "./ProjectUatScript"
import { UserVO } from "./UserVO"

export interface TimesheetReport {
    timesheetId: number
    timesheetEntryId: number
    userId: number
    userName: string
    userEmail: string
    userEmpId: string
    projectCode: string
    projectName: string
    task: string
    taskDescription: string
    totalHours: string
    billable: boolean
    approved: boolean
    rejected: boolean
    approverId: number
    approverName: string
    approverEmail: string
    approverEmpId: string
    approverComment: string
  }
  

export interface TimesheetReportList {
    content: TimesheetReport[];
    totalElements: number;
}