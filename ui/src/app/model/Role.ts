import { Status } from "./enumerator/Status";

export class Role {
    id: number;
    name: string;
    status: Status;
    constructor() {
        this.id = null;
        this.name = '';
        this.status = Status.ACTIVE;
    }
}