import {Injectable} from '@angular/core';
import {Sbgb} from './sbgb.model';

@Injectable({providedIn: 'root'})
export class SbgbComparisonService {

  isModified(current: Sbgb, reference: Sbgb): boolean {
    if (!reference) return true;
    return !this.structuresEqual(current, reference) || !this.colorsEqual(current, reference) || current.name !== reference.name;
  }

  private structuresEqual(a: Sbgb, b: Sbgb): boolean {
    const s1 = a.imageStructure;
    const s2 = b.imageStructure;
    return Number(s1.width) === Number(s2.width)
      && Number(s1.height) === Number(s2.height)
      && Number(s1.seed) === Number(s2.seed)
      && Number(s1.octaves) === Number(s2.octaves)
      && Number(s1.persistence) === Number(s2.persistence)
      && Number(s1.lacunarity) === Number(s2.lacunarity)
      && Number(s1.scale) === Number(s2.scale)
      && s1.noiseType === s2.noiseType
      && s1.preset === s2.preset
      && s1.useMultiLayer === s2.useMultiLayer;
  }

  private colorsEqual(a: Sbgb, b: Sbgb): boolean {
    const c1 = a.imageColor;
    const c2 = b.imageColor;
    return c1.back === c2.back
      && c1.middle === c2.middle
      && c1.fore === c2.fore
      && Number(c1.backThreshold) === Number(c2.backThreshold)
      && Number(c1.middleThreshold) === Number(c2.middleThreshold)
      && c1.interpolationType === c2.interpolationType;
  }
}
