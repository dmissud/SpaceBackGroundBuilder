import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {GalaxyImageDTO, GalaxyRequestCmd} from './galaxy.model';
import {ApiService} from '../common/api.service';

@Injectable({
  providedIn: 'root'
})
export class GalaxyService {

  private readonly galaxyApiUrl: string;
  public readonly galaxySaved$ = new Subject<void>();

  constructor(
    private http: HttpClient,
    private apiService: ApiService
  ) {
    this.galaxyApiUrl = `${this.apiService.appUrl}/galaxies`;
  }

  getAllGalaxies(): Observable<GalaxyImageDTO[]> {
    return this.http.get<GalaxyImageDTO[]>(this.galaxyApiUrl);
  }

  buildGalaxy(request: GalaxyRequestCmd): Observable<Blob> {
    return this.http.post(`${this.galaxyApiUrl}/build`, request, {
      responseType: 'blob'
    });
  }

  createGalaxy(request: GalaxyRequestCmd): Observable<GalaxyImageDTO> {
    return this.http.post<GalaxyImageDTO>(`${this.galaxyApiUrl}/create`, request);
  }
}
