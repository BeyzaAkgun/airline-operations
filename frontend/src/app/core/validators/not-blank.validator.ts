import type { AbstractControl, ValidationErrors } from '@angular/forms';

export function notBlankValidator(control:AbstractControl):ValidationErrors|null{
    const value:unknown=control.value;
    if (typeof value === "string" && value.trim() === "") {
    return { blank: true };
}
    
    
    return null;
}

