import { SelectionModel } from "@angular/cdk/collections";
import { Incident } from "./Incident";
import { UserVO } from "./UserVO";

interface AssignUser {
    agentList: UserVO[];
    canAssign: boolean;
    selection: SelectionModel<Incident>;
}