export class Base {
    createdDate: Date;
    modifiedDate: Date;
    version: number;
    modifiedBy: number;
    createdBy: number;

    constructor() {
        this.createdDate = null;
        this.modifiedDate = null;
        this.version = null;
        this.modifiedBy = null;
        this.createdBy = null;
    }
}