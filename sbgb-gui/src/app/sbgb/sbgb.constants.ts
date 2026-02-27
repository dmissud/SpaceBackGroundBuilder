export const STAR_RATING_VALUES = [1, 2, 3, 4, 5] as const;

export enum PresetName {
  CUSTOM = 'CUSTOM',
  DEEP_SPACE = 'DEEP_SPACE',
  NEBULA_DENSE = 'NEBULA_DENSE',
  STARFIELD = 'STARFIELD',
  COSMIC_DUST = 'COSMIC_DUST',
  GALAXY = 'GALAXY'
}

export const INFO_MESSAGES = {
  IMAGE_GENERATED: 'Image generated successfully',
  RENDER_SAVED: 'Ciel étoilé sauvegardé avec succès',
  SAVE_ERROR_PREFIX: 'Erreur lors de la sauvegarde: '
} as const;
