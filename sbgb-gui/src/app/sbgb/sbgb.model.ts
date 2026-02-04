export interface Image {
  width: number;
  height: number;
  seed: number;
  octaves: number;
  persistence: number;
  lacunarity: number;
  scale: number;
}

export interface Color {
  back: string;
  middle: string;
  fore: string;
  backThreshold: number;
  middleThreshold: number;
}

export interface Sbgb {
  id?: string;
  name?: string;
  description?: string;
  imageStructure: Image;
  imageColor: Color;
}



