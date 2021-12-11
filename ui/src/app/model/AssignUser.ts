import { SelectionModel } from "@angular/cdk/collections";
import { Incident } from "./Incident";
import { UserVO } from "./UserVO";

export interface AssignUser {
    agentList: UserVO[];
    canAssign: boolean;
    selectedValues: Map<number, string>;
}