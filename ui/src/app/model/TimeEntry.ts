export class TimeEntry {
    purpose: string;
    time: string;
    newEntry: boolean;
    constructor() {
        this.purpose = null;
        this.time = null;
        this.newEntry = false;
    }
}