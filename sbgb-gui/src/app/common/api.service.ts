import {Injectable} from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {environment} from '../../environments/environment';
import {catchError} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  appUrl = environment.API_DATA_PROVIDER_URL;

  constructor(private http: HttpClient) {
  }

  private static getHeaders(): HttpHeaders {
    let headers = new HttpHeaders();
    headers = headers.append('Content-Type', 'application/json');
    return headers;
  }

  get(url: string, urlParams?: HttpParams): Observable<any> {
    return this.http.get(this.appUrl + url, {headers: ApiService.getHeaders(), params: urlParams})
      .pipe(catchError(error => this.handleError(error)));
  }

  post(url: string, body: object, urlParams?: { responseType: string }): Observable<any> {
    return this.http.post(this.appUrl + url, JSON.stringify(body), {
      headers: ApiService.getHeaders(),
      params: urlParams
    })
      .pipe(catchError(error => this.handleError(error)));
  }

  put(url: string, body: object, urlParams?: HttpParams): Observable<any> {
    return this.http.put(this.appUrl + url, JSON.stringify(body), {headers: ApiService.getHeaders(), params: urlParams})
      .pipe(catchError(error => this.handleError(error)));
  }

  delete(url: string, urlParams?: HttpParams): Observable<any> {
    return this.http.delete(this.appUrl + url, {headers: ApiService.getHeaders(), params: urlParams})
      .pipe(catchError(error => this.handleError(error)));
  }

  uploadFile(url: string, flux: File, name: string): Observable<any> {
    const formData = new FormData();
    formData.append(name, flux);

    return this.http.post(this.appUrl + url, formData,
      {
        observe: 'response',
        reportProgress: true
      })
      .pipe(catchError(error => this.handleError(error)));
  }

  private handleError(error: HttpErrorResponse): Observable<any> {
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      console.error('An error occurred:', error.error.message);
    } else {
      console.error(
        `Backend returned code ${error.status}, ` +
        `body was: ${error.error}`);
      console.log(error);
      switch (error.status) {
        case 0:
          break;
        case 401:
          break;
        case 403:
        case 404:
        case 409:
        case 500:
          break;
      }
    }
    // Return an observable with a user-facing error message.
    return throwError(() => new Error(
      'Something bad happened; please try again later.'));
  }
}
