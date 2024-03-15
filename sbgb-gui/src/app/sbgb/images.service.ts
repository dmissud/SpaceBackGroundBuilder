import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Sbgb} from "./sbgb.model";

@Injectable({
  providedIn: 'root'
})
export class ImagesService {

  private imagesApiUrl: string = '/images';
  appUrl = environment.API_DATA_PROVIDER_URL;

  constructor(private http: HttpClient) { }

  buildImage(sbgb: Sbgb): Observable<HttpResponse<Blob>> {
    return this.http.post<Blob>(this.appUrl + this.imagesApiUrl,
      sbgb,
      { observe: 'response', responseType: 'blob' as 'json' });
  }
}
