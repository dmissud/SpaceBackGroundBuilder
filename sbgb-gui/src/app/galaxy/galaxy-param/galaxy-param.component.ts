import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatAccordion} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {GalaxyService} from "../galaxy.service";
import {GalaxyImageDTO, GalaxyRequestCmd} from "../galaxy.model";
import {BasicInfoSectionComponent} from "./sections/basic-info-section.component";
import {PresetsSectionComponent} from "./sections/presets-section.component";
import {SpiralStructureSectionComponent} from "./sections/spiral-structure-section.component";
import {VoronoiStructureSectionComponent} from "./sections/voronoi-structure-section.component";
import {EllipticalStructureSectionComponent} from "./sections/elliptical-structure-section.component";
import {RingStructureSectionComponent} from "./sections/ring-structure-section.component";
import {IrregularStructureSectionComponent} from "./sections/irregular-structure-section.component";
import {CoreRadiusSectionComponent} from "./sections/core-radius-section.component";
import {NoiseTextureSectionComponent} from "./sections/noise-texture-section.component";
import {VisualEffectsSectionComponent} from "./sections/visual-effects-section.component";
import {ColorsSectionComponent} from "./sections/colors-section.component";


@Component({
  selector: 'app-galaxy-param',
  imports: [
    ReactiveFormsModule,
    MatAccordion,
    MatAccordion,
    MatIcon,
    MatTooltip,
    BasicInfoSectionComponent,
    PresetsSectionComponent,
    SpiralStructureSectionComponent,
    VoronoiStructureSectionComponent,
    EllipticalStructureSectionComponent,
    RingStructureSectionComponent,
    IrregularStructureSectionComponent,
    CoreRadiusSectionComponent,
    NoiseTextureSectionComponent,
    VisualEffectsSectionComponent,
    ColorsSectionComponent
  ],
  templateUrl: './galaxy-param.component.html',
  styleUrl: './galaxy-param.component.scss'
})
export class GalaxyParamComponent implements OnInit {

  @ViewChild(MatAccordion) accordion!: MatAccordion;

  galaxyForm: FormGroup;
  generatedImageUrl: string | null = null;
  isGenerating = false;
  loadedGalaxyName: string | null = null;
  allPanelsExpanded = false;
  private isModifiedSinceBuild: boolean = true;
  private builtGalaxyParams: GalaxyRequestCmd | null = null;

  // Representative colors for each predefined palette (Space, Core, Arms, Outer)
  private readonly PALETTE_COLORS: Record<string, { space: string, core: string, arms: string, outer: string }> = {
    'CLASSIC': {space: '#05050f', core: '#fffadc', arms: '#b4c8ff', outer: '#3c5078'},
    'NEBULA': {space: '#05050f', core: '#f0e6c8', arms: '#781e64', outer: '#3c8cd2'},
    'WARM': {space: '#0a0505', core: '#fffaf0', arms: '#c86428', outer: '#8c3214'},
    'COLD': {space: '#02050f', core: '#f0faff', arms: '#288cc8', outer: '#145096'},
    'INFRARED': {space: '#000005', core: '#ffffc8', arms: '#b43c1e', outer: '#641414'},
    'EMERALD': {space: '#020a08', core: '#f0fff0', arms: '#28b478', outer: '#146446'}
  };

  constructor(
    private readonly galaxyService: GalaxyService,
    private readonly snackBar: MatSnackBar,
    private readonly fb: FormBuilder
  ) {
    this.galaxyForm = this.fb.group({
      name: new FormControl<string | null>('Galaxy', [Validators.required]),
      width: new FormControl<number | null>(4000, [Validators.required, Validators.min(100)]),
      height: new FormControl<number | null>(4000, [Validators.required, Validators.min(100)]),
      seed: new FormControl<number | null>(Math.floor(Math.random() * 1000000)),
      galaxyType: new FormControl<string | null>('SPIRAL'),
      coreSize: new FormControl<number | null>(0.05, [Validators.required]),
      galaxyRadius: new FormControl<number | null>(1500, [Validators.required]),
      warpStrength: new FormControl<number | null>(0, [Validators.min(0), Validators.max(300)]),
      noiseParameters: this.fb.group({
        octaves: new FormControl<number | null>(4, [Validators.required]),
        persistence: new FormControl<number | null>(0.5, [Validators.required]),
        lacunarity: new FormControl<number | null>(2, [Validators.required]),
        scale: new FormControl<number | null>(200, [Validators.required])
      }),
      spiralParameters: this.fb.group({
        numberOfArms: new FormControl<number | null>(2, [Validators.required, Validators.min(1)]),
        armWidth: new FormControl<number | null>(80, [Validators.required, Validators.min(10)]),
        armRotation: new FormControl<number | null>(4, [Validators.required])
      }),
      voronoiParameters: this.fb.group({
        clusterCount: new FormControl<number | null>(80, [Validators.min(5), Validators.max(500)]),
        clusterSize: new FormControl<number | null>(60, [Validators.min(10)]),
        clusterConcentration: new FormControl<number | null>(0.7, [Validators.min(0), Validators.max(1)])
      }),
      ellipticalParameters: this.fb.group({
        sersicIndex: new FormControl<number | null>(4, [Validators.min(0.5), Validators.max(10)]),
        axisRatio: new FormControl<number | null>(0.7, [Validators.min(0.1), Validators.max(1)]),
        orientationAngle: new FormControl<number | null>(0, [Validators.min(0), Validators.max(360)])
      }),
      ringParameters: this.fb.group({
        ringRadius: new FormControl<number | null>(900, [Validators.min(50)]),
        ringWidth: new FormControl<number | null>(150, [Validators.min(10)]),
        ringIntensity: new FormControl<number | null>(1, [Validators.min(0.1), Validators.max(2)]),
        coreToRingRatio: new FormControl<number | null>(0.3, [Validators.min(0), Validators.max(1)])
      }),
      irregularParameters: this.fb.group({
        irregularity: new FormControl<number | null>(0.8, [Validators.min(0), Validators.max(1)]),
        irregularClumpCount: new FormControl<number | null>(15, [Validators.min(5), Validators.max(50)]),
        irregularClumpSize: new FormControl<number | null>(80, [Validators.min(20)])
      }),
      starFieldParameters: this.fb.group({
        enabled: new FormControl<boolean | null>(false),
        density: new FormControl<number | null>(0, [Validators.min(0), Validators.max(0.01)]),
        maxStarSize: new FormControl<number | null>(4, [Validators.min(1), Validators.max(10)]),
        diffractionSpikes: new FormControl<boolean | null>(false),
        spikeCount: new FormControl<number | null>(4, [Validators.min(4), Validators.max(8)])
      }),
      multiLayerNoiseParameters: this.fb.group({
        enabled: new FormControl<boolean | null>(false),
        macroLayerScale: new FormControl<number | null>(0.3, [Validators.min(0.1), Validators.max(5)]),
        macroLayerWeight: new FormControl<number | null>(0.5, [Validators.min(0), Validators.max(1)]),
        mesoLayerScale: new FormControl<number | null>(1, [Validators.min(0.1), Validators.max(5)]),
        mesoLayerWeight: new FormControl<number | null>(0.35, [Validators.min(0), Validators.max(1)]),
        microLayerScale: new FormControl<number | null>(3, [Validators.min(0.1), Validators.max(10)]),
        microLayerWeight: new FormControl<number | null>(0.15, [Validators.min(0), Validators.max(1)])
      }),
      colorParameters: this.fb.group({
        colorPalette: new FormControl<string | null>('CLASSIC'),
        spaceBackgroundColor: new FormControl<string | null>('#050510'),
        coreColor: new FormControl<string | null>('#FFFADC'),
        armColor: new FormControl<string | null>('#B4C8FF'),
        outerColor: new FormControl<string | null>('#3C5078')
      })
    });
  }

  ngOnInit(): void {
    this.galaxyForm.valueChanges.subscribe(() => {
      this.isModifiedSinceBuild = true;
    });

    // Sync predefined palette selection to individual colors
    this.galaxyForm.get('colorParameters.colorPalette')?.valueChanges.subscribe(palette => {
      if (palette && palette !== 'CUSTOM' && this.PALETTE_COLORS[palette]) {
        const colors = this.PALETTE_COLORS[palette];
        this.galaxyForm.patchValue({
          colorParameters: {
            spaceBackgroundColor: colors.space,
            coreColor: colors.core,
            armColor: colors.arms,
            outerColor: colors.outer
          }
        }, {emitEvent: false}); // Prevent infinite loop Triggering the individual color watcher
      }
    });

    // Detect manual overrides to individual colors and switch palette to CUSTOM
    const customColorsToWatch = ['spaceBackgroundColor', 'coreColor', 'armColor', 'outerColor'];
    customColorsToWatch.forEach(controlName => {
      this.galaxyForm.get(`colorParameters.${controlName}`)?.valueChanges.subscribe(() => {
        const currentPalette = this.galaxyForm.get('colorParameters.colorPalette')?.value;
        if (currentPalette !== 'CUSTOM') {
          this.galaxyForm.patchValue({
            colorParameters: {
              colorPalette: 'CUSTOM'
            }
          }, {emitEvent: false});
        }
      });
    });
  }

  onGalaxyTypeChange(): void {
    const galaxyType = this.galaxyForm.controls['galaxyType'].value;

    this.galaxyForm.controls['spiralParameters'].disable();
    this.galaxyForm.controls['voronoiParameters'].disable();
    this.galaxyForm.controls['ellipticalParameters'].disable();
    this.galaxyForm.controls['ringParameters'].disable();
    this.galaxyForm.controls['irregularParameters'].disable();

    if (galaxyType === 'SPIRAL') {
      this.galaxyForm.controls['spiralParameters'].enable();
    } else if (galaxyType === 'VORONOI_CLUSTER') {
      this.galaxyForm.controls['voronoiParameters'].enable();
    } else if (galaxyType === 'ELLIPTICAL') {
      this.galaxyForm.controls['ellipticalParameters'].enable();
    } else if (galaxyType === 'RING') {
      this.galaxyForm.controls['ringParameters'].enable();
    } else if (galaxyType === 'IRREGULAR') {
      this.galaxyForm.controls['irregularParameters'].enable();
    }
  }

  getParametersSummary(): string {
    const form = this.galaxyForm.value;
    const type = form.galaxyType || 'SPIRAL';
    const width = form.width || 4000;
    const height = form.height || 4000;
    const seed = form.seed || 0;
    const palette = form.colorParameters?.colorPalette || 'CLASSIC';
    const warp = form.warpStrength || 0;
    const starDensity = form.starFieldParameters?.density || 0;
    const spikes = form.starFieldParameters?.diffractionSpikes || false;
    const multiLayer = form.multiLayerNoiseParameters?.enabled || false;

    let summary = `${String(type)} galaxy`;

    // Add structure-specific info
    if (type === 'SPIRAL') {
      const arms = form.spiralParameters?.numberOfArms || 2;
      const rotation = form.spiralParameters?.armRotation || 4;
      summary += ` with ${String(arms)} arm${arms > 1 ? 's' : ''}, rotation ${String(rotation)}`;
    } else if (type === 'VORONOI_CLUSTER') {
      const clusters = form.voronoiParameters?.clusterCount || 80;
      summary += ` with ${String(clusters)} clusters`;
    } else if (type === 'ELLIPTICAL') {
      const sersic = form.ellipticalParameters?.sersicIndex || 4.0;
      const ratio = form.ellipticalParameters?.axisRatio || 0.7;
      summary += ` (Sersic n=${String(sersic)}, axis ratio ${String(ratio)})`;
    } else if (type === 'RING') {
      const radius = form.ringParameters?.ringRadius || 900;
      const ringWidth = form.ringParameters?.ringWidth || 150;
      summary += ` (ring radius ${String(radius)}px, width ${String(ringWidth)}px)`;
    } else if (type === 'IRREGULAR') {
      const irregularity = form.irregularParameters?.irregularity || 0.8;
      const clumps = form.irregularParameters?.irregularClumpCount || 15;
      summary += ` (irregularity ${String(irregularity)}, ${String(clumps)} clumps)`;
    }

    summary += `, ${String(width)}x${String(height)}px`;
    summary += `, ${String(palette)} palette`;

    if (warp > 0) {
      summary += `, warp ${String(warp)}`;
    }

    if (multiLayer) {
      summary += `, multi-layer noise`;
    }

    if (starDensity > 0) {
      summary += `, star density ${String(starDensity)}`;
      if (spikes) {
        summary += ' with diffraction spikes';
      }
    }

    summary += `, seed ${String(seed)}`;

    return summary;
  }

  generateGalaxy(): void {
    if (this.galaxyForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', { duration: 3000 });
      return;
    }

    this.isGenerating = true;
    this.generatedImageUrl = null; // Clear previous image
    const request: GalaxyRequestCmd = this.galaxyForm.value;

    // Auto-generate description
    request.description = this.getParametersSummary();

    this.galaxyService.buildGalaxy(request).subscribe({
      next: (blob) => {
        this.generatedImageUrl = URL.createObjectURL(blob);
        this.isGenerating = false;
        this.isModifiedSinceBuild = false;
        this.builtGalaxyParams = structuredClone(this.galaxyForm.value) as GalaxyRequestCmd;
        this.snackBar.open('Galaxy generated successfully!', 'Close', { duration: 3000 });
      },
      error: (error) => {
        console.error('Error generating galaxy:', error);
        this.snackBar.open('Error generating galaxy', 'Close', { duration: 3000 });
        this.isGenerating = false;
      }
    });
  }

  saveGalaxy(): void {
    if (this.galaxyForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', { duration: 3000 });
      return;
    }

    const request: GalaxyRequestCmd = this.galaxyForm.value;
    request.description = this.getParametersSummary(); // Auto-generate description
    request.forceUpdate = false;

    // First attempt without forceUpdate
    this.isGenerating = true;
    this.galaxyService.createGalaxy(request).subscribe({
      next: (galaxy) => {
        this.isGenerating = false;
        this.loadedGalaxyName = galaxy.name;
        this.snackBar.open(`Galaxy "${galaxy.name}" saved successfully!`, 'Close', { duration: 3000 });
      },
      error: (error) => {
        this.isGenerating = false;
        if (error.status === 409) {
          // Name already exists - ask for confirmation
          const confirmUpdate = confirm(`La galaxie "${request.name}" existe deja. Voulez-vous la remplacer ?`);
          if (confirmUpdate) {
            request.forceUpdate = true;
            this.isGenerating = true;
            this.galaxyService.createGalaxy(request).subscribe({
              next: (galaxy) => {
                this.isGenerating = false;
                this.loadedGalaxyName = galaxy.name;
                this.snackBar.open(`Galaxy "${galaxy.name}" updated successfully!`, 'Close', { duration: 3000 });
              },
              error: (err) => {
                console.error('Error updating galaxy:', err);
                this.snackBar.open('Error updating galaxy', 'Close', { duration: 3000 });
                this.isGenerating = false;
              }
            });
          }
        } else {
          console.error('Error saving galaxy:', error);
          this.snackBar.open('Error saving galaxy', 'Close', { duration: 3000 });
        }
      }
    });
  }

  randomizeSeed(): void {
    this.galaxyForm.patchValue({
      seed: Math.floor(Math.random() * 1000000)
    });
  }

  randomizeCore(): void {
    this.galaxyForm.patchValue({
      coreSize: +(0.02 + Math.random() * 0.08).toFixed(3),  // 0.02-0.10
      galaxyRadius: Math.floor(1000 + Math.random() * 1000)  // 1000-2000
    });
  }

  randomizeNoise(): void {
    this.galaxyForm.patchValue({
      noiseParameters: {
        octaves: Math.floor(3 + Math.random() * 6),  // 3-8
        persistence: +(0.4 + Math.random() * 0.4).toFixed(2),  // 0.4-0.8
        lacunarity: +(1.5 + Math.random() * 1.5).toFixed(1),  // 1.5-3.0
        scale: Math.floor(100 + Math.random() * 200)  // 100-300
      }
    });
  }

  randomizeWarping(): void {
    this.galaxyForm.patchValue({
      warpStrength: Math.floor(Math.random() * 150)  // 0-150
    });
  }

  randomizeStarField(): void {
    const density = +(Math.random() * 0.005).toFixed(4);  // 0-0.005
    const hasSpikes = Math.random() > 0.7;
    this.galaxyForm.patchValue({
      starFieldParameters: {
        density: density,
        maxStarSize: Math.floor(2 + Math.random() * 7),  // 2-8
        diffractionSpikes: hasSpikes,
        spikeCount: hasSpikes ? (Math.random() > 0.5 ? 6 : 4) : 4
      }
    });
  }

  randomizeMultiLayerNoise(): void {
    this.galaxyForm.patchValue({
      multiLayerNoiseParameters: {
        macroLayerScale: +(0.1 + Math.random() * 4.9).toFixed(1),
        macroLayerWeight: +(Math.random()).toFixed(2),
        mesoLayerScale: +(0.1 + Math.random() * 4.9).toFixed(1),
        mesoLayerWeight: +(Math.random()).toFixed(2),
        microLayerScale: +(0.1 + Math.random() * 9.9).toFixed(1),
        microLayerWeight: +(Math.random()).toFixed(2)
      }
    });
  }

  randomizeSpiral(): void {
    this.galaxyForm.patchValue({
      spiralParameters: {
        numberOfArms: Math.floor(2 + Math.random() * 3),  // 2-4
        armWidth: Math.floor(60 + Math.random() * 60),  // 60-120
        armRotation: +(3 + Math.random() * 4).toFixed(1)  // 3-7
      }
    });
  }

  randomizeVoronoi(): void {
    this.galaxyForm.patchValue({
      voronoiParameters: {
        clusterCount: Math.floor(30 + Math.random() * 170),  // 30-200
        clusterSize: Math.floor(40 + Math.random() * 80),  // 40-120
        clusterConcentration: +(0.3 + Math.random() * 0.6).toFixed(2)  // 0.3-0.9
      }
    });
  }

  randomizeElliptical(): void {
    this.galaxyForm.patchValue({
      ellipticalParameters: {
        sersicIndex: +(1 + Math.random() * 7).toFixed(1),  // 1-8
        axisRatio: +(0.3 + Math.random() * 0.7).toFixed(2),  // 0.3-1.0
        orientationAngle: Math.floor(Math.random() * 360)  // 0-360
      }
    });
  }

  randomizeRing(): void {
    const galaxyRadius = this.galaxyForm.value.galaxyRadius || 1500;
    const ringRadius = Math.floor(galaxyRadius * 0.5 + Math.random() * galaxyRadius * 0.3);  // 50-80% of galaxy radius
    this.galaxyForm.patchValue({
      ringParameters: {
        ringRadius: ringRadius,
        ringWidth: Math.floor(80 + Math.random() * 200),  // 80-280
        ringIntensity: +(0.6 + Math.random() * 1.0).toFixed(1),  // 0.6-1.6
        coreToRingRatio: +(0.1 + Math.random() * 0.6).toFixed(2)  // 0.1-0.7
      }
    });
  }

  randomizeIrregular(): void {
    this.galaxyForm.patchValue({
      irregularParameters: {
        irregularity: +(0.6 + Math.random() * 0.4).toFixed(2),  // 0.6-1.0
        irregularClumpCount: Math.floor(8 + Math.random() * 30),  // 8-38
        irregularClumpSize: Math.floor(50 + Math.random() * 100)  // 50-150
      }
    });
  }

  randomizeStructure(): void {
    const galaxyType = this.galaxyForm.value.galaxyType;
    this.randomizeCore();
    this.randomizeNoise();
    this.randomizeWarping();
    this.randomizeStarField();
    this.randomizeMultiLayerNoise();

    switch (galaxyType) {
      case 'SPIRAL':
        this.randomizeSpiral();
        break;
      case 'VORONOI_CLUSTER':
        this.randomizeVoronoi();
        break;
      case 'ELLIPTICAL':
        this.randomizeElliptical();
        break;
      case 'RING':
        this.randomizeRing();
        break;
      case 'IRREGULAR':
        this.randomizeIrregular();
        break;
    }
  }

  randomizeCustomColors(): void {
    const randomHex = () => '#' + Math.floor(Math.random() * 16777215).toString(16).padStart(6, '0');

    this.galaxyForm.patchValue({
      colorParameters: {
        colorPalette: 'CUSTOM',
        spaceBackgroundColor: randomHex(),
        coreColor: randomHex(),
        armColor: randomHex(),
        outerColor: randomHex()
      }
    });
  }

  randomizeAll(): void {
    this.randomizeSeed();
    this.randomizeStructure();

    // For "Randomize All", it's better to pick a cohesive palette rather than completely random chaotic colors
    const palettes = ['CLASSIC', 'NEBULA', 'WARM', 'COLD', 'INFRARED', 'EMERALD'];
    const randomPalette = palettes[Math.floor(Math.random() * palettes.length)];
    this.galaxyForm.patchValue({
      colorParameters: {
        colorPalette: randomPalette
      }
    });
  }

  canBuild(): boolean {
    return this.isModifiedSinceBuild && !this.isGenerating;
  }

  getBuildTooltip(): string {
    if (!this.isModifiedSinceBuild) {
      return 'Aucune modification détectée. Modifiez les paramètres pour pouvoir générer une nouvelle image.';
    }
    return 'Générer l\'image avec les paramètres actuels';
  }

  canSave(): boolean {
    return !!this.generatedImageUrl && !this.isModifiedSinceBuild && !this.isGenerating;
  }

  getSaveTooltip(): string {
    if (!this.generatedImageUrl) {
      return 'Vous devez d\'abord générer une image (Générer aperçu) avant de pouvoir la sauvegarder.';
    }
    if (this.isModifiedSinceBuild) {
      return 'Vous avez modifié les paramètres. Générez l\'image (Générer aperçu) avant de sauvegarder.';
    }
    return 'Sauvegarder cette image dans la bibliothèque';
  }

  canDownload(): boolean {
    return !!this.generatedImageUrl && !this.isModifiedSinceBuild && !this.isGenerating;
  }

  getDownloadTooltip(): string {
    if (this.isGenerating) {
      return 'Generation en cours, veuillez patienter.';
    }
    if (!this.generatedImageUrl) {
      return 'Vous devez d\'abord generer une image (Generer apercu) avant de pouvoir la telecharger.';
    }
    if (this.isModifiedSinceBuild) {
      return 'Vous devez regenerer l\'image (Generer apercu) car les parametres ont ete modifies.';
    }
    return 'Telecharger l\'image generee sur votre PC';
  }

  downloadImage(): void {
    if (!this.generatedImageUrl) return;

    const link = document.createElement('a');
    link.href = this.generatedImageUrl;
    const name = this.galaxyForm.controls['name'].value || 'galaxy-image';
    link.download = `${name}.png`;
    link.click();
  }

  loadGalaxy(galaxy: GalaxyImageDTO): void {
    const s = galaxy.galaxyStructure;
    const galaxyType = s.galaxyType || 'SPIRAL';

    this.galaxyForm.patchValue({
      name: galaxy.name,
      description: galaxy.description,
      width: s.width,
      height: s.height,
      seed: s.seed,
      galaxyType: galaxyType,
      coreSize: s.coreSize,
      galaxyRadius: s.galaxyRadius,
      warpStrength: s.warpStrength,
      noiseParameters: s.noiseParameters,
      spiralParameters: s.spiralParameters,
      voronoiParameters: s.voronoiParameters,
      ellipticalParameters: s.ellipticalParameters,
      ringParameters: s.ringParameters,
      irregularParameters: s.irregularParameters,
      starFieldParameters: s.starFieldParameters,
      multiLayerNoiseParameters: s.multiLayerNoiseParameters,
      colorParameters: s.colorParameters
    });

    this.loadedGalaxyName = galaxy.name;
    this.onGalaxyTypeChange();
    this.generateGalaxy();
  }

  loadPreset(preset: string): void {
    switch (preset) {
      case 'CLASSIC':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 2, armWidth: 80, armRotation: 4 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'BARRED':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 2, armWidth: 100, armRotation: 3 },
          noiseParameters: { octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 150 }
        });
        break;
      case 'MULTI_ARM':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 3, armWidth: 70, armRotation: 5 },
          noiseParameters: { octaves: 6, persistence: 0.55, lacunarity: 2.1, scale: 180 }
        });
        break;
      case 'SPIRAL_GRAND_DESIGN':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1600,
          spiralParameters: { numberOfArms: 2, armWidth: 120, armRotation: 3.5 },
          noiseParameters: { octaves: 4, persistence: 0.45, lacunarity: 2.0, scale: 220 },
          warpStrength: 50
        });
        break;
      case 'SPIRAL_FLOCCULENT':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1400,
          spiralParameters: { numberOfArms: 4, armWidth: 60, armRotation: 6 },
          noiseParameters: { octaves: 8, persistence: 0.7, lacunarity: 2.8, scale: 120 },
          warpStrength: 80
        });
        break;
      case 'SPIRAL_TIGHTLY_WOUND':
        this.galaxyForm.patchValue({
          coreSize: 0.07,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 3, armWidth: 50, armRotation: 8 },
          noiseParameters: { octaves: 5, persistence: 0.5, lacunarity: 2.2, scale: 180 }
        });
        break;
      case 'VORONOI_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          voronoiParameters: { clusterCount: 80, clusterSize: 60, clusterConcentration: 0.7 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'VORONOI_DENSE':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          voronoiParameters: { clusterCount: 200, clusterSize: 40, clusterConcentration: 0.85 },
          noiseParameters: { octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 150 }
        });
        break;
      case 'VORONOI_SPARSE':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          voronoiParameters: { clusterCount: 30, clusterSize: 90, clusterConcentration: 0.4 },
          noiseParameters: { octaves: 3, persistence: 0.4, lacunarity: 1.8, scale: 250 }
        });
        break;
      case 'VORONOI_GLOBULAR':
        this.galaxyForm.patchValue({
          coreSize: 0.02,
          galaxyRadius: 1300,
          voronoiParameters: { clusterCount: 150, clusterSize: 30, clusterConcentration: 0.95 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 180 }
        });
        break;
      case 'ELLIPTICAL_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          ellipticalParameters: { sersicIndex: 4.0, axisRatio: 0.7, orientationAngle: 45 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'ELLIPTICAL_ROUND':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          ellipticalParameters: { sersicIndex: 2.0, axisRatio: 0.95, orientationAngle: 0 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'ELLIPTICAL_FLAT':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          ellipticalParameters: { sersicIndex: 6.0, axisRatio: 0.4, orientationAngle: 30 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'ELLIPTICAL_GIANT':
        this.galaxyForm.patchValue({
          coreSize: 0.1,
          galaxyRadius: 1800,
          ellipticalParameters: { sersicIndex: 8.0, axisRatio: 0.85, orientationAngle: 0 },
          noiseParameters: { octaves: 3, persistence: 0.4, lacunarity: 1.8, scale: 250 }
        });
        break;
      case 'RING_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 900, ringWidth: 150, ringIntensity: 1.0, coreToRingRatio: 0.3 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'RING_WIDE':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 1000, ringWidth: 250, ringIntensity: 0.8, coreToRingRatio: 0.2 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 }
        });
        break;
      case 'RING_BRIGHT':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 800, ringWidth: 120, ringIntensity: 1.2, coreToRingRatio: 0.5 },
          noiseParameters: { octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 180 }
        });
        break;
      case 'RING_THIN':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 950, ringWidth: 80, ringIntensity: 1.5, coreToRingRatio: 0.2 },
          noiseParameters: { octaves: 4, persistence: 0.45, lacunarity: 2.0, scale: 220 }
        });
        break;
      case 'RING_DOUBLE':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1600,
          ringParameters: { ringRadius: 700, ringWidth: 200, ringIntensity: 0.9, coreToRingRatio: 0.4 },
          noiseParameters: { octaves: 6, persistence: 0.65, lacunarity: 2.3, scale: 160 },
          warpStrength: 30
        });
        break;
      case 'IRREGULAR_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          irregularParameters: { irregularity: 0.8, irregularClumpCount: 15, irregularClumpSize: 80 },
          noiseParameters: { octaves: 6, persistence: 0.7, lacunarity: 2.5, scale: 150 }
        });
        break;
      case 'IRREGULAR_CHAOTIC':
        this.galaxyForm.patchValue({
          coreSize: 0.02,
          galaxyRadius: 1500,
          irregularParameters: { irregularity: 0.95, irregularClumpCount: 25, irregularClumpSize: 60 },
          noiseParameters: { octaves: 8, persistence: 0.8, lacunarity: 3.0, scale: 120 }
        });
        break;
      case 'IRREGULAR_DWARF':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          irregularParameters: { irregularity: 0.7, irregularClumpCount: 8, irregularClumpSize: 100 },
          noiseParameters: { octaves: 5, persistence: 0.6, lacunarity: 2.0, scale: 200 }
        });
        break;
    }
  }

  toggleAllPanels(): void {
    if (this.allPanelsExpanded) {
      this.accordion.closeAll();
      this.allPanelsExpanded = false;
    } else {
      this.accordion.openAll();
      this.allPanelsExpanded = true;
    }
  }
}
