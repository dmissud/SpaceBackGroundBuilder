import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatAccordion} from "@angular/material/expansion";
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

  galaxyForm!: FormGroup;
  generatedImageUrl: string | null = null;
  isGenerating = false;
  loadedGalaxyName: string | null = null;
  private isModifiedSinceBuild: boolean = true;
  private builtGalaxyParams: any = null;

  constructor(
    private galaxyService: GalaxyService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.galaxyForm = new FormGroup({
      name: new FormControl('Galaxy', [Validators.required]),
      width: new FormControl(4000, [Validators.required, Validators.min(100)]),
      height: new FormControl(4000, [Validators.required, Validators.min(100)]),
      seed: new FormControl(Math.floor(Math.random() * 1000000)),
      galaxyType: new FormControl('SPIRAL'),
      coreSize: new FormControl(0.05, [Validators.required]),
      galaxyRadius: new FormControl(1500, [Validators.required]),
      warpStrength: new FormControl(0, [Validators.min(0), Validators.max(300)]),
      noiseParameters: new FormGroup({
        octaves: new FormControl(4, [Validators.required]),
        persistence: new FormControl(0.5, [Validators.required]),
        lacunarity: new FormControl(2.0, [Validators.required]),
        scale: new FormControl(200, [Validators.required])
      }),
      spiralParameters: new FormGroup({
        numberOfArms: new FormControl(2, [Validators.required, Validators.min(1)]),
        armWidth: new FormControl(80, [Validators.required, Validators.min(10)]),
        armRotation: new FormControl(4, [Validators.required])
      }),
      voronoiParameters: new FormGroup({
        clusterCount: new FormControl(80, [Validators.min(5), Validators.max(500)]),
        clusterSize: new FormControl(60, [Validators.min(10)]),
        clusterConcentration: new FormControl(0.7, [Validators.min(0), Validators.max(1)])
      }),
      ellipticalParameters: new FormGroup({
        sersicIndex: new FormControl(4.0, [Validators.min(0.5), Validators.max(10)]),
        axisRatio: new FormControl(0.7, [Validators.min(0.1), Validators.max(1)]),
        orientationAngle: new FormControl(0, [Validators.min(0), Validators.max(360)])
      }),
      ringParameters: new FormGroup({
        ringRadius: new FormControl(900, [Validators.min(50)]),
        ringWidth: new FormControl(150, [Validators.min(10)]),
        ringIntensity: new FormControl(1.0, [Validators.min(0.1), Validators.max(2)]),
        coreToRingRatio: new FormControl(0.3, [Validators.min(0), Validators.max(1)])
      }),
      irregularParameters: new FormGroup({
        irregularity: new FormControl(0.8, [Validators.min(0), Validators.max(1)]),
        irregularClumpCount: new FormControl(15, [Validators.min(5), Validators.max(50)]),
        irregularClumpSize: new FormControl(80, [Validators.min(20)])
      }),
      starFieldParameters: new FormGroup({
        density: new FormControl(0, [Validators.min(0), Validators.max(0.01)]),
        maxStarSize: new FormControl(4, [Validators.min(1), Validators.max(10)]),
        diffractionSpikes: new FormControl(false),
        spikeCount: new FormControl(4, [Validators.min(4), Validators.max(8)])
      }),
      multiLayerNoiseParameters: new FormGroup({
        enabled: new FormControl(false),
        macroLayerScale: new FormControl(0.3, [Validators.min(0.1), Validators.max(5)]),
        macroLayerWeight: new FormControl(0.5, [Validators.min(0), Validators.max(1)]),
        mesoLayerScale: new FormControl(1.0, [Validators.min(0.1), Validators.max(5)]),
        mesoLayerWeight: new FormControl(0.35, [Validators.min(0), Validators.max(1)]),
        microLayerScale: new FormControl(3.0, [Validators.min(0.1), Validators.max(10)]),
        microLayerWeight: new FormControl(0.15, [Validators.min(0), Validators.max(1)])
      }),
      colorParameters: new FormGroup({
        colorPalette: new FormControl('CLASSIC'),
        spaceBackgroundColor: new FormControl('#050510'),
        coreColor: new FormControl('#FFFADC'),
        armColor: new FormControl('#B4C8FF'),
        outerColor: new FormControl('#3C5078')
      })
    });

    // Track form changes to detect modifications
    this.galaxyForm.valueChanges.subscribe(() => {
      this.isModifiedSinceBuild = true;
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

    let summary = `${type} galaxy`;

    // Add structure-specific info
    if (type === 'SPIRAL') {
      const arms = form.spiralParameters?.numberOfArms || 2;
      const rotation = form.spiralParameters?.armRotation || 4;
      summary += ` with ${arms} arm${arms > 1 ? 's' : ''}, rotation ${rotation}`;
    } else if (type === 'VORONOI_CLUSTER') {
      const clusters = form.voronoiParameters?.clusterCount || 80;
      summary += ` with ${clusters} clusters`;
    } else if (type === 'ELLIPTICAL') {
      const sersic = form.ellipticalParameters?.sersicIndex || 4.0;
      const ratio = form.ellipticalParameters?.axisRatio || 0.7;
      summary += ` (Sersic n=${sersic}, axis ratio ${ratio})`;
    } else if (type === 'RING') {
      const radius = form.ringParameters?.ringRadius || 900;
      const width = form.ringParameters?.ringWidth || 150;
      summary += ` (ring radius ${radius}px, width ${width}px)`;
    } else if (type === 'IRREGULAR') {
      const irregularity = form.irregularParameters?.irregularity || 0.8;
      const clumps = form.irregularParameters?.irregularClumpCount || 15;
      summary += ` (irregularity ${irregularity}, ${clumps} clumps)`;
    }

    summary += `, ${width}x${height}px`;
    summary += `, ${palette} palette`;

    if (warp > 0) {
      summary += `, warp ${warp}`;
    }

    if (multiLayer) {
      summary += `, multi-layer noise`;
    }

    if (starDensity > 0) {
      summary += `, star density ${starDensity}`;
      if (spikes) {
        summary += ' with diffraction spikes';
      }
    }

    summary += `, seed ${seed}`;

    return summary;
  }

  generateGalaxy(): void {
    if (this.galaxyForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', {duration: 3000});
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
        this.builtGalaxyParams = JSON.parse(JSON.stringify(this.galaxyForm.value));
        this.snackBar.open('Galaxy generated successfully!', 'Close', {duration: 3000});
      },
      error: (error) => {
        console.error('Error generating galaxy:', error);
        this.snackBar.open('Error generating galaxy', 'Close', {duration: 3000});
        this.isGenerating = false;
      }
    });
  }

  saveGalaxy(): void {
    if (this.galaxyForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', {duration: 3000});
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
        this.snackBar.open(`Galaxy "${galaxy.name}" saved successfully!`, 'Close', {duration: 3000});
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
                this.snackBar.open(`Galaxy "${galaxy.name}" updated successfully!`, 'Close', {duration: 3000});
              },
              error: (err) => {
                console.error('Error updating galaxy:', err);
                this.snackBar.open('Error updating galaxy', 'Close', {duration: 3000});
                this.isGenerating = false;
              }
            });
          }
        } else {
          console.error('Error saving galaxy:', error);
          this.snackBar.open('Error saving galaxy', 'Close', {duration: 3000});
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

  randomizeColors(): void {
    const palettes = ['CLASSIC', 'NEBULA', 'WARM', 'COLD', 'INFRARED', 'EMERALD'];
    const randomPalette = palettes[Math.floor(Math.random() * palettes.length)];
    this.galaxyForm.patchValue({
      colorParameters: {
        colorPalette: randomPalette
      }
    });
  }

  randomizeAll(): void {
    this.randomizeSeed();
    this.randomizeStructure();
    this.randomizeColors();
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
          spiralParameters: {numberOfArms: 2, armWidth: 80, armRotation: 4},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'BARRED':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          spiralParameters: {numberOfArms: 2, armWidth: 100, armRotation: 3},
          noiseParameters: {octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 150}
        });
        break;
      case 'MULTI_ARM':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          spiralParameters: {numberOfArms: 3, armWidth: 70, armRotation: 5},
          noiseParameters: {octaves: 6, persistence: 0.55, lacunarity: 2.1, scale: 180}
        });
        break;
      case 'SPIRAL_GRAND_DESIGN':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1600,
          spiralParameters: {numberOfArms: 2, armWidth: 120, armRotation: 3.5},
          noiseParameters: {octaves: 4, persistence: 0.45, lacunarity: 2.0, scale: 220},
          warpStrength: 50
        });
        break;
      case 'SPIRAL_FLOCCULENT':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1400,
          spiralParameters: {numberOfArms: 4, armWidth: 60, armRotation: 6},
          noiseParameters: {octaves: 8, persistence: 0.7, lacunarity: 2.8, scale: 120},
          warpStrength: 80
        });
        break;
      case 'SPIRAL_TIGHTLY_WOUND':
        this.galaxyForm.patchValue({
          coreSize: 0.07,
          galaxyRadius: 1500,
          spiralParameters: {numberOfArms: 3, armWidth: 50, armRotation: 8},
          noiseParameters: {octaves: 5, persistence: 0.5, lacunarity: 2.2, scale: 180}
        });
        break;
      case 'VORONOI_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          voronoiParameters: {clusterCount: 80, clusterSize: 60, clusterConcentration: 0.7},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'VORONOI_DENSE':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          voronoiParameters: {clusterCount: 200, clusterSize: 40, clusterConcentration: 0.85},
          noiseParameters: {octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 150}
        });
        break;
      case 'VORONOI_SPARSE':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          voronoiParameters: {clusterCount: 30, clusterSize: 90, clusterConcentration: 0.4},
          noiseParameters: {octaves: 3, persistence: 0.4, lacunarity: 1.8, scale: 250}
        });
        break;
      case 'VORONOI_GLOBULAR':
        this.galaxyForm.patchValue({
          coreSize: 0.02,
          galaxyRadius: 1300,
          voronoiParameters: {clusterCount: 150, clusterSize: 30, clusterConcentration: 0.95},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 180}
        });
        break;
      case 'ELLIPTICAL_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          ellipticalParameters: {sersicIndex: 4.0, axisRatio: 0.7, orientationAngle: 45},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'ELLIPTICAL_ROUND':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          ellipticalParameters: {sersicIndex: 2.0, axisRatio: 0.95, orientationAngle: 0},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'ELLIPTICAL_FLAT':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          ellipticalParameters: {sersicIndex: 6.0, axisRatio: 0.4, orientationAngle: 30},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'ELLIPTICAL_GIANT':
        this.galaxyForm.patchValue({
          coreSize: 0.1,
          galaxyRadius: 1800,
          ellipticalParameters: {sersicIndex: 8.0, axisRatio: 0.85, orientationAngle: 0},
          noiseParameters: {octaves: 3, persistence: 0.4, lacunarity: 1.8, scale: 250}
        });
        break;
      case 'RING_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          ringParameters: {ringRadius: 900, ringWidth: 150, ringIntensity: 1.0, coreToRingRatio: 0.3},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'RING_WIDE':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          ringParameters: {ringRadius: 1000, ringWidth: 250, ringIntensity: 0.8, coreToRingRatio: 0.2},
          noiseParameters: {octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200}
        });
        break;
      case 'RING_BRIGHT':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          ringParameters: {ringRadius: 800, ringWidth: 120, ringIntensity: 1.2, coreToRingRatio: 0.5},
          noiseParameters: {octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 180}
        });
        break;
      case 'RING_THIN':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          ringParameters: {ringRadius: 950, ringWidth: 80, ringIntensity: 1.5, coreToRingRatio: 0.2},
          noiseParameters: {octaves: 4, persistence: 0.45, lacunarity: 2.0, scale: 220}
        });
        break;
      case 'RING_DOUBLE':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1600,
          ringParameters: {ringRadius: 700, ringWidth: 200, ringIntensity: 0.9, coreToRingRatio: 0.4},
          noiseParameters: {octaves: 6, persistence: 0.65, lacunarity: 2.3, scale: 160},
          warpStrength: 30
        });
        break;
      case 'IRREGULAR_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          irregularParameters: {irregularity: 0.8, irregularClumpCount: 15, irregularClumpSize: 80},
          noiseParameters: {octaves: 6, persistence: 0.7, lacunarity: 2.5, scale: 150}
        });
        break;
      case 'IRREGULAR_CHAOTIC':
        this.galaxyForm.patchValue({
          coreSize: 0.02,
          galaxyRadius: 1500,
          irregularParameters: {irregularity: 0.95, irregularClumpCount: 25, irregularClumpSize: 60},
          noiseParameters: {octaves: 8, persistence: 0.8, lacunarity: 3.0, scale: 120}
        });
        break;
      case 'IRREGULAR_DWARF':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          irregularParameters: {irregularity: 0.7, irregularClumpCount: 8, irregularClumpSize: 100},
          noiseParameters: {octaves: 5, persistence: 0.6, lacunarity: 2.0, scale: 200}
        });
        break;
    }
  }
}
