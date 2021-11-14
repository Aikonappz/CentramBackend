import { FormGroup } from '@angular/forms';
import * as moment from 'moment';
import { AppUtility } from '../config/AppUtility';

// custom validator to check that two fields 
export function StartEndDateValidation(startDate: string, endDate: string) {
    return (formGroup: FormGroup) => {
        const start = formGroup.controls[startDate];
        const end = formGroup.controls[endDate];
        if (end.errors && !end.errors.mustGreaterThanStartDate) {
            // return if another validator has already found an error on the matchingControl
            return;
        }
        // set error on matchingControl if validation fails
        let startD = moment((start.value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
        let endD = moment((end.value), AppUtility.APP_VIEW_DATEPICKER_OP_DATE_FORMAT).toDate();
        if (endD <= startD) {
            end.setErrors({ mustGreaterThanStartDate: true });
        } else {
            end.setErrors(null);
        }
    }
}