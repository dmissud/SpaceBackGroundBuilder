import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import {GalaxyBaseStructureDto, GalaxyCosmeticRenderDto, GalaxyPersistedState, GalaxyRequestCmd} from './galaxy.model';
import {ApiService} from '../common/api.service';

@Injectable({
  providedIn: 'root'
})
export class GalaxyService {

  private readonly galaxyApiUrl: string;
  public readonly galaxySaved$ = new Subject<void>();
  private persistedState: GalaxyPersistedState | null = null;

  constructor(
    private http: HttpClient,
    private apiService: ApiService
  ) {
    this.galaxyApiUrl = `${this.apiService.appUrl}/galaxy`;
  }

  saveState(state: GalaxyPersistedState): void {
    this.persistedState = state;
  }

  getState(): GalaxyPersistedState | null {
    return this.persistedState;
  }

  clearState(): void {
    this.persistedState = null;
  }

  getAllBases(): Observable<GalaxyBaseStructureDto[]> {
    return this.http.get<GalaxyBaseStructureDto[]>(`${this.galaxyApiUrl}/bases`);
  }

  buildGalaxy(request: GalaxyRequestCmd): Observable<Blob> {
    return this.http.post(`${this.galaxyApiUrl}/build`, request, {
      responseType: 'blob'
    });
  }

  rateGalaxy(request: GalaxyRequestCmd): Observable<GalaxyCosmeticRenderDto> {
    return this.http.post<GalaxyCosmeticRenderDto>(`${this.galaxyApiUrl}/renders/rate`, request);
  }

  getRendersForBase(baseId: string): Observable<GalaxyCosmeticRenderDto[]> {
    return this.http.get<GalaxyCosmeticRenderDto[]>(`${this.galaxyApiUrl}/bases/${baseId}/renders`);
  }

  deleteRender(renderId: string): Observable<void> {
    return this.http.delete<void>(`${this.galaxyApiUrl}/renders/${renderId}`);
  }

  deleteRendersByBase(baseId: string): Observable<void> {
    return this.http.delete<void>(`${this.galaxyApiUrl}/bases/${baseId}/renders`);
  }

  reapplyCosmetics(baseId: string, request: GalaxyRequestCmd): Observable<GalaxyCosmeticRenderDto[]> {
    return this.http.post<GalaxyCosmeticRenderDto[]>(`${this.galaxyApiUrl}/bases/${baseId}/reapply`, request);
  }
}
