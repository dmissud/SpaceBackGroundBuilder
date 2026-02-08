export interface GalaxyImageDTO {
  id: string;
  name: string;
  description: string;
  note: number;
  galaxyStructure: GalaxyStructureDTO;
}

export interface GalaxyStructureDTO {
  width: number;
  height: number;
  seed: number;
  galaxyType?: string;
  numberOfArms: number;
  armWidth: number;
  armRotation: number;
  coreSize: number;
  galaxyRadius: number;
  noiseOctaves: number;
  noisePersistence: number;
  noiseLacunarity: number;
  noiseScale: number;
  clusterCount?: number;
  clusterSize?: number;
  clusterConcentration?: number;
  sersicIndex?: number;
  axisRatio?: number;
  orientationAngle?: number;
  ringRadius?: number;
  ringWidth?: number;
  ringIntensity?: number;
  coreToRingRatio?: number;
  irregularity?: number;
  irregularClumpCount?: number;
  irregularClumpSize?: number;
  warpStrength?: number;
  colorPalette?: string;
  starDensity?: number;
  maxStarSize?: number;
  diffractionSpikes?: boolean;
  spikeCount?: number;
  spaceBackgroundColor?: string;
  coreColor?: string;
  armColor?: string;
  outerColor?: string;
}

export interface GalaxyRequestCmd {
  name: string;
  description: string;
  width: number;
  height: number;
  seed: number;
  forceUpdate?: boolean;
  galaxyType?: string;
  numberOfArms: number;
  armWidth: number;
  armRotation: number;
  coreSize: number;
  galaxyRadius: number;
  noiseOctaves: number;
  noisePersistence: number;
  noiseLacunarity: number;
  noiseScale: number;
  clusterCount?: number;
  clusterSize?: number;
  clusterConcentration?: number;
  sersicIndex?: number;
  axisRatio?: number;
  orientationAngle?: number;
  ringRadius?: number;
  ringWidth?: number;
  ringIntensity?: number;
  coreToRingRatio?: number;
  irregularity?: number;
  irregularClumpCount?: number;
  irregularClumpSize?: number;
  warpStrength?: number;
  colorPalette?: string;
  starDensity?: number;
  maxStarSize?: number;
  diffractionSpikes?: boolean;
  spikeCount?: number;
  spaceBackgroundColor?: string;
  coreColor?: string;
  armColor?: string;
  outerColor?: string;
}
