import { AbstractControl, FormControl } from "@angular/forms";
export function requiredFileType(type: string[]) {
  return function(control: FormControl) {
    // return (control: AbstractControl): { [key: string]: any } | null => {
    const file = control.value;
    var existValue: boolean = false;
    if (file) {

      var path = file.replace(/^.*[\\\/]/, "");

      const extension = path.split(".")[1].toUpperCase();
      for (var i = 0; i < type.length; i++) {
        let typeFile = type[i].toUpperCase();
        if (typeFile === extension.toUpperCase()) {
          existValue = true;
        }
      }
      if (existValue == true) {
        return null;
      } else {
        return {
          requiredFileType: true
        };
      }
    }
    return null;
  };
}