export interface Image {
  width: number;
  height: number;
  seed: number;
}

export interface Color {
  back: string;
  middle: string;
  fore: string;
  backThreshold: number;
  middleThreshold: number;
}

export interface Sbgb {
  image: Image;
  color: Color;
}



