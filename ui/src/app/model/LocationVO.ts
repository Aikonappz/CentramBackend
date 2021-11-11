import { Base } from "./Base";
import { Status } from "./enumerator/Status";

export class LocationVO extends Base {
    id: number;
    name: string;
    status: Status;
    constructor() {
        super();
        this.id = null;
        this.name = '';
        this.status = Status.ACTIVE;
    }
}