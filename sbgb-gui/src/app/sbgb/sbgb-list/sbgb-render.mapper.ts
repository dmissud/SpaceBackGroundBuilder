import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from '../sbgb.model';

export function toSbgbFromRender(base: NoiseBaseStructureDto, render: NoiseCosmeticRenderDto): Sbgb {
  return {
    id: base.id,
    description: base.description,
    note: render.note,
    imageStructure: toImageStructure(base),
    imageColor: toImageColor(render)
  };
}

export function groupRendersByBaseId(renders: NoiseCosmeticRenderDto[]): Record<string, NoiseCosmeticRenderDto[]> {
  return renders.reduce((acc, render) => {
    const baseId = render.baseStructureId;
    return {...acc, [baseId]: [...(acc[baseId] ?? []), render]};
  }, {} as Record<string, NoiseCosmeticRenderDto[]>);
}

function toImageStructure(base: NoiseBaseStructureDto) {
  return {
    width: base.width,
    height: base.height,
    seed: base.seed,
    octaves: base.octaves,
    persistence: base.persistence,
    lacunarity: base.lacunarity,
    scale: base.scale,
    noiseType: base.noiseType,
    preset: 'CUSTOM',
    useMultiLayer: base.useMultiLayer
  };
}

function toImageColor(render: NoiseCosmeticRenderDto) {
  return {
    back: render.back,
    middle: render.middle,
    fore: render.fore,
    backThreshold: render.backThreshold,
    middleThreshold: render.middleThreshold,
    interpolationType: render.interpolationType,
    transparentBackground: render.transparentBackground
  };
}
