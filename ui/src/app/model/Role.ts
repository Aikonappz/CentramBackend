import { Status } from "./enumerator/Status";

export class Role {
    id: number;
    name: string;
    displayName: string;
    description: string;
    status: any;
    constructor() {
        this.id = null;
        this.name = '';
        this.status = Status.ACTIVE;
    }
}