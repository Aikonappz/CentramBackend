import { UserVO } from "./UserVO";

export interface IAssignUser {
    agentList: UserVO[];
    canAssign: boolean;
    selectedValues: Map<number, string>;
}