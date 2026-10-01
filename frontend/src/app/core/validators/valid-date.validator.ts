import { ValidationErrors,AbstractControl } from "@angular/forms";


export function validDateValidator(
    control: AbstractControl
): ValidationErrors | null {
    const value: unknown = control.value;

    if (value === "") {
        return null; 
    }

    if (typeof value !== "string") {
        return { invalidDate: true };
    }

    if (Number.isNaN(new Date(value).getTime())) {
        return { invalidDate: true };
    }

    return null;
}