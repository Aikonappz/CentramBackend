import { Base } from "./Base";
import { Holiday } from "./Holiday";
import { LocationVO } from "./LocationVO";
import { Organisation } from "./Organisation";

export class HolidayCalender extends Base {
    id: number;
    year: string;
    location: any;
    holidays: Holiday[];
    organisation: Organisation;
    constructor() {
        super();
        this.id = null;
        this.year = '';
        this.location = {};
        this.organisation = new Organisation();
    }
}
export interface HolidayCalenderList {
    content: HolidayCalender[];
    totalElements: number;
}