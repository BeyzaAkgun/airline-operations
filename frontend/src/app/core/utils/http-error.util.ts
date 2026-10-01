import { HttpErrorResponse } from '@angular/common/http';
import { isApiErrorResponse } from '../guards/api-error-response.guard';
    
    
    export function getErrorMessage(error:unknown,fallback:string):string{
    if(error instanceof HttpErrorResponse){
      const body:unknown=error.error;

      if(isApiErrorResponse(body)){
        return body.message;
      }
    }
    return fallback;

  }
