import { Account } from "./Account";
import { Base } from "./Base";
import { Holiday } from "./Holiday";
import { Organisation } from "./Organisation";

export class HolidayCalender extends Base {
    id: number;
    year: string;
    location: any;
    holidays: Holiday[];
    organisation: Organisation;
    account: Account;
    constructor() {
        super();
        this.id = null;
        this.year = '';
        this.location = {};
        this.organisation = new Organisation();
        this.account = new Account();
    }
}
export interface HolidayCalenderList {
    content: HolidayCalender[];
    totalElements: number;
}