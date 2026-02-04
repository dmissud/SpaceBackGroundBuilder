export interface Layer {
  name: string;
  enabled: boolean;
  octaves: number;
  persistence: number;
  lacunarity: number;
  scale: number;
  opacity: number;
  blendMode: string;
  seedOffset: number;
}

export interface Image {
  width: number;
  height: number;
  seed: number;
  octaves: number;
  persistence: number;
  lacunarity: number;
  scale: number;
  preset: string;
  useMultiLayer: boolean;
  layers?: Layer[];
}

export interface Color {
  back: string;
  middle: string;
  fore: string;
  backThreshold: number;
  middleThreshold: number;
  interpolationType: string;
}

export interface Sbgb {
  id?: string;
  name?: string;
  description?: string;
  imageStructure: Image;
  imageColor: Color;
}



