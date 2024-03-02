import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ImagesService {

  private imagesApiUrl: string = '/images';
  appUrl = environment.API_DATA_PROVIDER_URL;

  constructor(private http: HttpClient) { }

  buildImage(height: number, width: number, seed: number): Observable<HttpResponse<Blob>> {
    const payload = {
      height: height,
      width: width,
      seed: seed
    };
    return this.http.post<Blob>(this.appUrl + this.imagesApiUrl, payload, { observe: 'response', responseType: 'blob' as 'json' });
  }
}
