import {Injectable} from '@angular/core';

@Injectable({providedIn: 'root'})
export class HttpErrorHandlerService {
  extractMessage(error: any): string {
    return error?.error?.message
      || error?.message
      || error?.statusText
      || 'An unknown error occurred';
  }
}
