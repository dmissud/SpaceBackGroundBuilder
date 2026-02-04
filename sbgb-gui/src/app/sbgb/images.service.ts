import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Sbgb} from "./sbgb.model";

@Injectable({
  providedIn: 'root'
})
export class ImagesService {

  private imagesApiUrl: string = '/images/build';
  private imagesSaveApiUrl: string = '/images/create';
  private imagesListApiUrl: string = '/images';
  appUrl = environment.API_DATA_PROVIDER_URL;

  constructor(private http: HttpClient) {
  }

  getImages(): Observable<Sbgb[]> {
    return this.http.get<Sbgb[]>(this.appUrl + this.imagesListApiUrl);
  }

  saveImage(sbgb: Sbgb, forceUpdate: boolean = false): Observable<Sbgb> {
    const payload = {
      name: sbgb.name,
      description: sbgb.description,
      forceUpdate: forceUpdate,
      sizeCmd: sbgb.imageStructure,
      colorCmd: sbgb.imageColor
    };
    return this.http.post<Sbgb>(this.appUrl + this.imagesSaveApiUrl, payload);
  }

  buildImage(sbgb: Sbgb): Observable<HttpResponse<Blob>> {
    const payload = {
      sizeCmd: sbgb.imageStructure,
      colorCmd: sbgb.imageColor
    };
    return this.http.post<Blob>(this.appUrl + this.imagesApiUrl, payload, {
      observe: 'response', responseType: 'blob' as 'json'
    });
  }
}
