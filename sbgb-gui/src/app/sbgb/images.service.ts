import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpResponse} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from "./sbgb.model";


@Injectable({
  providedIn: 'root'
})
export class ImagesService {

  private buildApiUrl: string = '/images/build';
  private rateApiUrl: string = '/images/renders/rate';
  private basesApiUrl: string = '/images/bases';
  appUrl = environment.API_DATA_PROVIDER_URL;

  constructor(private http: HttpClient) {
  }

  getBases(): Observable<NoiseBaseStructureDto[]> {
    return this.http.get<NoiseBaseStructureDto[]>(this.appUrl + this.basesApiUrl);
  }

  rateRender(sbgb: Sbgb, note: number): Observable<NoiseCosmeticRenderDto> {
    const payload = {
      note,
      sizeCmd: sbgb.imageStructure,
      colorCmd: sbgb.imageColor
    };
    return this.http.post<NoiseCosmeticRenderDto>(this.appUrl + this.rateApiUrl, payload);
  }

  getRendersForBase(baseId: string): Observable<NoiseCosmeticRenderDto[]> {
    return this.http.get<NoiseCosmeticRenderDto[]>(`${this.appUrl}/images/bases/${baseId}/renders`);
  }

  deleteRender(id: string): Observable<void> {
    return this.http.delete<void>(`${this.appUrl}/images/renders/${id}`);
  }

  buildImage(sbgb: Sbgb): Observable<HttpResponse<Blob>> {
    const payload = {
      sizeCmd: sbgb.imageStructure,
      colorCmd: sbgb.imageColor
    };
    return this.http.post<Blob>(this.appUrl + this.buildApiUrl, payload, {
      observe: 'response', responseType: 'blob' as 'json'
    });
  }
}
