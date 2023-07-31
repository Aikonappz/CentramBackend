import { FormGroup } from '@angular/forms';
import * as moment from 'moment';
import { AppUtility } from '../config/AppUtility';

// custom validator 
export function TimeSheetDateValidation(selection: string, singleDate: string, rangeDate: string) {
    return (formGroup: FormGroup) => {
        if (formGroup.controls[selection].value == "DAILY") {
            if (formGroup.controls[singleDate].value == null || formGroup.controls[singleDate].value == "") {
                formGroup.controls[singleDate].setErrors({ required: true });
            } else {
                alert(formGroup.controls[singleDate].value);
                formGroup.controls[singleDate].setErrors(null);
            }
        } else if (formGroup.controls[selection].value == "RANGE") {
            if (formGroup.controls[rangeDate].value == null || formGroup.controls[rangeDate].value == "") {
                formGroup.controls[rangeDate].setErrors({ required: true });
            } else {
                formGroup.controls[rangeDate].setErrors(null);
            }
        }
    }
}