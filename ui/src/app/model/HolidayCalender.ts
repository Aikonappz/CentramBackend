import { Base } from "./Base";
import { Holiday } from "./Holiday";
import { Organisation } from "./Organisation";

export class HolidayCalender extends Base {
    id: number;
    year: string;
    holidays: Holiday[];
    organisation: Organisation;
    constructor() {
        super();
        this.id = null;
        this.year = '';
        this.organisation = new Organisation();
    }
}
export interface HolidayCalenderList {
    content: HolidayCalender[];
    totalElements: number;
}