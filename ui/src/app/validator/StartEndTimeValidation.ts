import { FormGroup } from '@angular/forms';
import * as moment from 'moment';
import { AppUtility } from '../config/AppUtility';

// custom validator to check that two fields 
export function StartEndTimeValidation(startTime: string, endTime: string) {
    return (formGroup: FormGroup) => {
        const start = formGroup.controls[startTime];
        const end = formGroup.controls[endTime];
        if (end.errors && !end.errors.mustGreaterThanStartTime) {
            // return if another validator has already found an error on the matchingControl
            return;
        }
        // set error on matchingControl if validation fails
        let startT = moment((start.value), AppUtility.APP_TIME_FORMAT);
        let endT = moment((end.value), AppUtility.APP_TIME_FORMAT);
        if (endT.isBefore(startT)) {
            end.setErrors({ mustGreaterThanStartTime: true });
        } else {
            end.setErrors(null);
        }
    }
}