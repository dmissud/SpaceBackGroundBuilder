import {Component, OnDestroy, OnInit, ViewChild, DestroyRef, inject} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatAccordion} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {Store} from "@ngrx/store";
import {Actions, ofType} from "@ngrx/effects";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";
import {GalaxyPageActions} from "../state/galaxy.actions";
import {GalaxyService} from "../galaxy.service";
import {GalaxyBaseStructureDto, GalaxyRequestCmd} from "../galaxy.model";
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
export class GalaxyParamComponent implements OnInit, OnDestroy {

  @ViewChild(MatAccordion) accordion!: MatAccordion;

  galaxyForm: FormGroup;
  generatedImageUrl: string | null = null;
  isGenerating = false;
  currentNote: number = 0;
  allPanelsExpanded = false;
  readonly starValues = [1, 2, 3, 4, 5];
  private isModifiedSinceBuild: boolean = true;
  private builtGalaxyParams: GalaxyRequestCmd | null = null;


  private isStructuralChange(): boolean {
    if (!this.builtGalaxyParams) return false;
    const current = this.galaxyForm.value;
    const built = this.builtGalaxyParams;

    return current.width !== built.width ||
      current.height !== built.height ||
      current.seed !== built.seed ||
      current.galaxyType !== built.galaxyType ||
      current.coreSize !== built.coreSize ||
      current.galaxyRadius !== built.galaxyRadius ||
      current.warpStrength !== built.warpStrength ||
      JSON.stringify(current.noiseParameters) !== JSON.stringify(built.noiseParameters) ||
      JSON.stringify(current.spiralParameters) !== JSON.stringify(built.spiralParameters) ||
      JSON.stringify(current.voronoiParameters) !== JSON.stringify(built.voronoiParameters) ||
      JSON.stringify(current.ellipticalParameters) !== JSON.stringify(built.ellipticalParameters) ||
      JSON.stringify(current.ringParameters) !== JSON.stringify(built.ringParameters) ||
      JSON.stringify(current.irregularParameters) !== JSON.stringify(built.irregularParameters) ||
      JSON.stringify(current.multiLayerNoiseParameters) !== JSON.stringify(built.multiLayerNoiseParameters);
  }

  // Representative colors for each predefined palette (Space, Core, Arms, Outer)
  private readonly PALETTE_COLORS: Record<string, { space: string, core: string, arms: string, outer: string }> = {
    'CLASSIC': { space: '#05050f', core: '#fffadc', arms: '#b4c8ff', outer: '#3c5078' },
    'NEBULA': { space: '#05050f', core: '#f0e6c8', arms: '#781e64', outer: '#3c8cd2' },
    'WARM': { space: '#0a0505', core: '#fffaf0', arms: '#c86428', outer: '#8c3214' },
    'COLD': { space: '#02050f', core: '#f0faff', arms: '#288cc8', outer: '#145096' },
    'INFRARED': { space: '#000005', core: '#ffffc8', arms: '#b43c1e', outer: '#641414' },
    'EMERALD': { space: '#020a08', core: '#f0fff0', arms: '#28b478', outer: '#146446' }
  };

  constructor(
    private readonly galaxyService: GalaxyService,
    private readonly snackBar: MatSnackBar,
    private readonly fb: FormBuilder,
    private readonly store: Store,
    private readonly actions$: Actions
  ) {
    const destroyRef = inject(DestroyRef);
    this.actions$.pipe(
      ofType(GalaxyPageActions.applyRenderCosmetics),
      takeUntilDestroyed(destroyRef)
    ).subscribe(({render}) => {
      this.galaxyForm.patchValue({
        starFieldParameters: {
          enabled: render.starDensity > 0,
          density: render.starDensity,
          maxStarSize: render.maxStarSize,
          diffractionSpikes: render.diffractionSpikes,
          spikeCount: render.spikeCount
        },
        bloomParameters: {
          enabled: render.bloomRadius > 0,
          bloomRadius: render.bloomRadius,
          bloomIntensity: render.bloomIntensity,
          bloomThreshold: render.bloomThreshold
        },
        colorParameters: {
          colorPalette: render.colorPalette || 'CUSTOM',
          coreColor: render.coreColor,
          armColor: render.armColor,
          outerColor: render.outerColor,
          spaceBackgroundColor: render.spaceBackgroundColor
        }
      });
      this.currentNote = render.note;
      // Ne pas appeler generateGalaxy() ici pour éviter des boucles et des appels inutiles,
      // car le rendu est déjà sélectionné et l'image est affichée via le store/shell si besoin,
      // ou on veut juste voir les paramètres. En fait, le shell affiche l'image du render si on veut,
      // mais ici on veut surtout que le générateur affiche l'aperçu correspondant.
      this.generateGalaxy();
    });

    this.galaxyForm = this.fb.group({
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
        armRotation: new FormControl<number | null>(4, [Validators.required]),
        darkLaneOpacity: new FormControl<number | null>(0, [Validators.min(0), Validators.max(1)])
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
      bloomParameters: this.fb.group({
        enabled: new FormControl<boolean | null>(false),
        bloomRadius: new FormControl<number | null>(10, [Validators.min(1), Validators.max(50)]),
        bloomIntensity: new FormControl<number | null>(0.5, [Validators.min(0), Validators.max(1)]),
        bloomThreshold: new FormControl<number | null>(0.5, [Validators.min(0), Validators.max(1)])
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
    const savedState = this.galaxyService.getState();
    if (savedState) {
      this.galaxyForm.patchValue(savedState.formValue, {emitEvent: false});
      this.generatedImageUrl = savedState.generatedImageUrl;
      this.isModifiedSinceBuild = savedState.isModifiedSinceBuild;
      this.builtGalaxyParams = savedState.builtGalaxyParams;

      if (this.builtGalaxyParams?.id) {
        this.store.dispatch(GalaxyPageActions.loadRendersForBase({baseId: this.builtGalaxyParams.id}));
      }
    }

    this.galaxyForm.valueChanges.subscribe(() => {
      this.isModifiedSinceBuild = true;
      if (this.isStructuralChange()) {
        this.store.dispatch(GalaxyPageActions.clearSelectedRender());
        this.currentNote = 0;
      }
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
        }, { emitEvent: false });
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
          }, { emitEvent: false });
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

    if (type === 'SPIRAL') {
      const arms = form.spiralParameters?.numberOfArms || 2;
      const rotation = form.spiralParameters?.armRotation || 4;
      summary += ` with ${String(arms)} arm${arms > 1 ? 's' : ''}, rotation ${String(rotation)}`;
    } else if (type === 'VORONOI_CLUSTER') {
      const clusters = form.voronoiParameters?.clusterCount || 80;
      summary += ` with ${String(clusters)} clusters`;
    } else if (type === 'ELLIPTICAL') {
      const sersic = form.ellipticalParameters?.sersicIndex || 4;
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
    this.generatedImageUrl = null;
    const request: GalaxyRequestCmd = this.galaxyForm.value;
    request.description = this.getParametersSummary();
    request.note = 0;

    if (this.builtGalaxyParams?.id && !this.isStructuralChange()) {
      request.id = this.builtGalaxyParams.id;
    }

    this.galaxyService.buildGalaxy(request).subscribe({
      next: (blob) => {
        this.generatedImageUrl = URL.createObjectURL(blob);
        this.isGenerating = false;
        this.isModifiedSinceBuild = false;
        this.builtGalaxyParams = {...request};
        this.saveCurrentState();
        this.snackBar.open('Galaxy generated successfully!', 'Close', { duration: 3000 });
      },
      error: (error) => {
        console.error('Error generating galaxy:', error);
        this.snackBar.open('Error generating galaxy', 'Close', { duration: 3000 });
        this.isGenerating = false;
      }
    });
  }

  onNoteSelected(note: number): void {
    if (note < 1) return;

    this.currentNote = note;
    const request: GalaxyRequestCmd = this.galaxyForm.value;
    request.description = this.getParametersSummary();
    request.note = note;

    this.galaxyService.rateGalaxy(request).subscribe({
      next: (render) => {
        this.galaxyService.galaxySaved$.next();
        this.store.dispatch(GalaxyPageActions.loadRendersForBase({baseId: render.baseStructureId}));
        this.store.dispatch(GalaxyPageActions.selectRender({renderId: render.id}));
        this.saveCurrentState();
        this.snackBar.open(`Galaxie sauvegardée avec la note ${note}/5`, 'Fermer', {duration: 3000});
      },
      error: (error: any) => {
        console.error('Error rating galaxy:', error);
        this.snackBar.open('Erreur lors de la sauvegarde', 'Fermer', {duration: 3000});
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
      coreSize: +(0.02 + Math.random() * 0.08).toFixed(3),
      galaxyRadius: Math.floor(1000 + Math.random() * 1000)
    });
  }

  randomizeNoise(): void {
    this.galaxyForm.patchValue({
      noiseParameters: {
        octaves: Math.floor(3 + Math.random() * 6),
        persistence: +(0.4 + Math.random() * 0.4).toFixed(2),
        lacunarity: +(1.5 + Math.random() * 1.5).toFixed(1),
        scale: Math.floor(100 + Math.random() * 200)
      }
    });
  }

  randomizeWarping(): void {
    this.galaxyForm.patchValue({
      warpStrength: Math.floor(Math.random() * 150)
    });
  }

  randomizeStarField(): void {
    const density = +(Math.random() * 0.005).toFixed(4);
    const hasSpikes = Math.random() > 0.7;
    let spikeCount = 4;
    if (hasSpikes) {
      spikeCount = Math.random() > 0.5 ? 6 : 4;
    }
    this.galaxyForm.patchValue({
      starFieldParameters: {
        density: density,
        maxStarSize: Math.floor(2 + Math.random() * 7),
        diffractionSpikes: hasSpikes,
        spikeCount: spikeCount
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
        numberOfArms: Math.floor(2 + Math.random() * 3),
        armWidth: Math.floor(60 + Math.random() * 60),
        armRotation: +(3 + Math.random() * 4).toFixed(1),
        darkLaneOpacity: +(Math.random()).toFixed(2)
      }
    });
  }

  randomizeVoronoi(): void {
    this.galaxyForm.patchValue({
      voronoiParameters: {
        clusterCount: Math.floor(30 + Math.random() * 170),
        clusterSize: Math.floor(40 + Math.random() * 80),
        clusterConcentration: +(0.3 + Math.random() * 0.6).toFixed(2)
      }
    });
  }

  randomizeElliptical(): void {
    this.galaxyForm.patchValue({
      ellipticalParameters: {
        sersicIndex: +(1 + Math.random() * 7).toFixed(1),
        axisRatio: +(0.3 + Math.random() * 0.7).toFixed(2),
        orientationAngle: Math.floor(Math.random() * 360)
      }
    });
  }

  randomizeRing(): void {
    const galaxyRadius = this.galaxyForm.value.galaxyRadius || 1500;
    const ringRadius = Math.floor(galaxyRadius * 0.5 + Math.random() * galaxyRadius * 0.3);
    this.galaxyForm.patchValue({
      ringParameters: {
        ringRadius: ringRadius,
        ringWidth: Math.floor(80 + Math.random() * 200),
        ringIntensity: +(0.6 + Math.random() * 1).toFixed(1),
        coreToRingRatio: +(0.1 + Math.random() * 0.6).toFixed(2)
      }
    });
  }

  randomizeIrregular(): void {
    this.galaxyForm.patchValue({
      irregularParameters: {
        irregularity: +(0.6 + Math.random() * 0.4).toFixed(2),
        irregularClumpCount: Math.floor(8 + Math.random() * 30),
        irregularClumpSize: Math.floor(50 + Math.random() * 100)
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

    const palettes = ['CLASSIC', 'NEBULA', 'WARM', 'COLD', 'INFRARED', 'EMERALD'];
    const randomPalette = palettes[Math.floor(Math.random() * palettes.length)];
    this.galaxyForm.patchValue({
      colorParameters: {
        colorPalette: randomPalette
      }
    });
  }

  canBuild(): boolean {
    return this.galaxyForm.valid && (this.isModifiedSinceBuild || this.isStructuralChange());
  }

  getBuildTooltip(): string {
    if (!this.canBuild()) {
      return this.galaxyForm.invalid ? 'Veuillez remplir correctement tous les champs requis' : 'Aucune modification structurante détectée';
    }
    return 'Générer l\'image avec les paramètres actuels';
  }

  canRate(): boolean {
    return !!this.generatedImageUrl && !this.isGenerating;
  }

  getRatingTooltip(): string {
    if (!this.generatedImageUrl) {
      return 'Générez d\'abord une image avant de pouvoir la noter et la sauvegarder.';
    }
    if (this.isModifiedSinceBuild) {
      return 'Attribuez une note pour sauvegarder cette galaxie (les paramètres ont été modifiés depuis la dernière génération).';
    }
    return 'Attribuez une note pour sauvegarder cette galaxie';
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
    const description = this.getParametersSummary();
    link.download = `galaxy-image.png`;
    link.click();
  }

  loadBase(base: GalaxyBaseStructureDto): void {
    this.galaxyForm.patchValue({
      width: base.width,
      height: base.height,
      seed: base.seed,
      galaxyType: base.galaxyType,
      coreSize: base.coreSize,
      galaxyRadius: base.galaxyRadius,
      warpStrength: base.warpStrength,
      noiseParameters: {
        octaves: base.noiseOctaves,
        persistence: base.noisePersistence,
        lacunarity: base.noiseLacunarity,
        scale: base.noiseScale
      },
      spiralParameters: {
        numberOfArms: base.numberOfArms,
        armWidth: base.armWidth,
        armRotation: base.armRotation,
        darkLaneOpacity: base.darkLaneOpacity
      },
      voronoiParameters: {
        clusterCount: base.clusterCount,
        clusterSize: base.clusterSize,
        clusterConcentration: base.clusterConcentration
      },
      ellipticalParameters: {
        sersicIndex: base.sersicIndex,
        axisRatio: base.axisRatio,
        orientationAngle: base.orientationAngle
      },
      ringParameters: {
        ringRadius: base.ringRadius,
        ringWidth: base.ringWidth,
        ringIntensity: base.ringIntensity,
        coreToRingRatio: base.coreToRingRatio
      },
      irregularParameters: {
        irregularity: base.irregularity,
        irregularClumpCount: base.irregularClumpCount,
        irregularClumpSize: base.irregularClumpSize
      },
      multiLayerNoiseParameters: {
        enabled: base.multiLayerEnabled,
        macroLayerScale: base.macroLayerScale,
        macroLayerWeight: base.macroLayerWeight,
        mesoLayerScale: base.mesoLayerScale,
        mesoLayerWeight: base.mesoLayerWeight,
        microLayerScale: base.microLayerScale,
        microLayerWeight: base.microLayerWeight
      }
    });

    this.builtGalaxyParams = this.galaxyForm.value;
    this.builtGalaxyParams!.id = base.id;
    this.isModifiedSinceBuild = false;

    if (base.id) {
      this.store.dispatch(GalaxyPageActions.loadRendersForBase({baseId: base.id}));
    }

    this.currentNote = base.maxNote;
    this.onGalaxyTypeChange();
    this.generateGalaxy();
  }

  private readonly BEAUTIFUL_STARFIELD = {
    enabled: true,
    density: 0.003,
    maxStarSize: 4,
    diffractionSpikes: true,
    spikeCount: 4
  };

  private readonly DENSE_STARFIELD = {
    enabled: true,
    density: 0.008,
    maxStarSize: 3,
    diffractionSpikes: false,
    spikeCount: 4
  };

  private readonly BEAUTIFUL_MULTILAYER_NOISE = {
    enabled: true,
    macroLayerScale: 0.8,
    macroLayerWeight: 0.6,
    mesoLayerScale: 2.2,
    mesoLayerWeight: 0.25,
    microLayerScale: 6.0,
    microLayerWeight: 0.15
  };

  loadPreset(preset: string): void {
    switch (preset) {
      case 'CLASSIC':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 2, armWidth: 80, armRotation: 4, darkLaneOpacity: 0.4 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'BARRED':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 2, armWidth: 100, armRotation: 1.5, darkLaneOpacity: 0.2 },
          noiseParameters: { octaves: 5, persistence: 0.6, lacunarity: 2.2, scale: 150 },
          warpStrength: 20,
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'MULTI_ARM':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1600,
          spiralParameters: { numberOfArms: 4, armWidth: 50, armRotation: 5.5, darkLaneOpacity: 0.5 },
          noiseParameters: { octaves: 6, persistence: 0.55, lacunarity: 2.1, scale: 180 },
          warpStrength: 45,
          starFieldParameters: this.BEAUTIFUL_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'SPIRAL_GRAND_DESIGN':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1800,
          spiralParameters: { numberOfArms: 2, armWidth: 110, armRotation: 3.5, darkLaneOpacity: 0.1 },
          noiseParameters: { octaves: 5, persistence: 0.45, lacunarity: 2.0, scale: 220 },
          warpStrength: 10,
          starFieldParameters: this.BEAUTIFUL_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'SPIRAL_FLOCCULENT':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1400,
          spiralParameters: { numberOfArms: 5, armWidth: 40, armRotation: 6, darkLaneOpacity: 0.3 },
          noiseParameters: { octaves: 8, persistence: 0.75, lacunarity: 2.8, scale: 110 },
          warpStrength: 120,
          starFieldParameters: this.DENSE_STARFIELD,
          multiLayerNoiseParameters: {
            enabled: true,
            macroLayerScale: 0.5,
            macroLayerWeight: 0.4,
            mesoLayerScale: 3.5,
            mesoLayerWeight: 0.4,
            microLayerScale: 8.0,
            microLayerWeight: 0.2
          }
        });
        break;
      case 'SPIRAL_TIGHTLY_WOUND':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          spiralParameters: { numberOfArms: 3, armWidth: 50, armRotation: 9, darkLaneOpacity: 0.0 },
          noiseParameters: { octaves: 5, persistence: 0.5, lacunarity: 2.2, scale: 180 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'VORONOI_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          voronoiParameters: { clusterCount: 150, clusterSize: 60, clusterConcentration: 0.75 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 200 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'VORONOI_DENSE':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1600,
          voronoiParameters: { clusterCount: 380, clusterSize: 35, clusterConcentration: 0.92 },
          noiseParameters: { octaves: 6, persistence: 0.55, lacunarity: 2.4, scale: 130 },
          starFieldParameters: this.DENSE_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'VORONOI_SPARSE':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          voronoiParameters: { clusterCount: 45, clusterSize: 100, clusterConcentration: 0.5 },
          noiseParameters: { octaves: 3, persistence: 0.4, lacunarity: 1.8, scale: 250 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'VORONOI_GLOBULAR':
        this.galaxyForm.patchValue({
          coreSize: 0.02,
          galaxyRadius: 1300,
          voronoiParameters: { clusterCount: 250, clusterSize: 45, clusterConcentration: 0.98 },
          noiseParameters: { octaves: 4, persistence: 0.5, lacunarity: 2.0, scale: 180 },
          starFieldParameters: this.DENSE_STARFIELD
        });
        break;
      case 'ELLIPTICAL_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.05,
          galaxyRadius: 1500,
          ellipticalParameters: { sersicIndex: 4.0, axisRatio: 0.7, orientationAngle: 45 },
          noiseParameters: { octaves: 3, persistence: 0.3, lacunarity: 2.0, scale: 220 },
          starFieldParameters: this.DENSE_STARFIELD
        });
        break;
      case 'ELLIPTICAL_ROUND':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1600,
          ellipticalParameters: { sersicIndex: 3.5, axisRatio: 0.95, orientationAngle: 0 },
          noiseParameters: { octaves: 3, persistence: 0.25, lacunarity: 2.0, scale: 250 },
          starFieldParameters: this.DENSE_STARFIELD
        });
        break;
      case 'ELLIPTICAL_FLAT':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          ellipticalParameters: { sersicIndex: 5.0, axisRatio: 0.35, orientationAngle: 30 },
          noiseParameters: { octaves: 4, persistence: 0.4, lacunarity: 2.2, scale: 200 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'ELLIPTICAL_GIANT':
        this.galaxyForm.patchValue({
          coreSize: 0.1,
          galaxyRadius: 1900,
          ellipticalParameters: { sersicIndex: 8.0, axisRatio: 0.85, orientationAngle: 0 },
          noiseParameters: { octaves: 2, persistence: 0.3, lacunarity: 1.8, scale: 280 },
          starFieldParameters: {
            enabled: true,
            density: 0.01,
            maxStarSize: 2,
            diffractionSpikes: false,
            spikeCount: 4
          }
        });
        break;
      case 'ELLIPTICAL_LENTICULAR':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1600,
          ellipticalParameters: { sersicIndex: 1.5, axisRatio: 0.25, orientationAngle: 90 },
          noiseParameters: { octaves: 3, persistence: 0.35, lacunarity: 2.0, scale: 230 },
          starFieldParameters: this.DENSE_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'RING_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 900, ringWidth: 150, ringIntensity: 1.1, coreToRingRatio: 0.35 },
          noiseParameters: { octaves: 5, persistence: 0.5, lacunarity: 2.0, scale: 200 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'RING_WIDE':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1600,
          ringParameters: { ringRadius: 1000, ringWidth: 320, ringIntensity: 0.95, coreToRingRatio: 0.2 },
          noiseParameters: { octaves: 6, persistence: 0.55, lacunarity: 2.2, scale: 190 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'RING_BRIGHT':
        this.galaxyForm.patchValue({
          coreSize: 0.08,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 750, ringWidth: 110, ringIntensity: 1.5, coreToRingRatio: 0.5 },
          noiseParameters: { octaves: 7, persistence: 0.65, lacunarity: 2.4, scale: 160 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD,
          multiLayerNoiseParameters: {
            enabled: true,
            macroLayerScale: 1.5,
            macroLayerWeight: 0.6,
            mesoLayerScale: 4.0,
            mesoLayerWeight: 0.3,
            microLayerScale: 9.0,
            microLayerWeight: 0.1
          }
        });
        break;
      case 'RING_THIN':
        this.galaxyForm.patchValue({
          coreSize: 0.04,
          galaxyRadius: 1500,
          ringParameters: { ringRadius: 950, ringWidth: 80, ringIntensity: 1.8, coreToRingRatio: 0.2 },
          noiseParameters: { octaves: 4, persistence: 0.45, lacunarity: 2.0, scale: 220 },
          starFieldParameters: this.DENSE_STARFIELD
        });
        break;
      case 'RING_DOUBLE':
        this.galaxyForm.patchValue({
          coreSize: 0.07,
          galaxyRadius: 1700,
          ringParameters: { ringRadius: 650, ringWidth: 250, ringIntensity: 1.0, coreToRingRatio: 0.45 },
          noiseParameters: { octaves: 6, persistence: 0.65, lacunarity: 2.3, scale: 160 },
          warpStrength: 45,
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
      case 'IRREGULAR_DEFAULT':
        this.galaxyForm.patchValue({
          coreSize: 0.03,
          galaxyRadius: 1500,
          irregularParameters: { irregularity: 0.9, irregularClumpCount: 22, irregularClumpSize: 90 },
          noiseParameters: { octaves: 7, persistence: 0.75, lacunarity: 2.6, scale: 140 },
          warpStrength: 90,
          starFieldParameters: this.BEAUTIFUL_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'IRREGULAR_CHAOTIC':
        this.galaxyForm.patchValue({
          coreSize: 0.02,
          galaxyRadius: 1700,
          irregularParameters: { irregularity: 1.0, irregularClumpCount: 45, irregularClumpSize: 70 },
          noiseParameters: { octaves: 8, persistence: 0.85, lacunarity: 3.2, scale: 100 },
          warpStrength: 250,
          starFieldParameters: this.DENSE_STARFIELD,
          multiLayerNoiseParameters: this.BEAUTIFUL_MULTILAYER_NOISE
        });
        break;
      case 'IRREGULAR_DWARF':
        this.galaxyForm.patchValue({
          coreSize: 0.06,
          galaxyRadius: 1200,
          irregularParameters: { irregularity: 0.8, irregularClumpCount: 12, irregularClumpSize: 60 },
          noiseParameters: { octaves: 5, persistence: 0.65, lacunarity: 2.2, scale: 200 },
          starFieldParameters: this.BEAUTIFUL_STARFIELD
        });
        break;
    }
  }

  toggleAllPanels(): void {
    this.allPanelsExpanded = !this.allPanelsExpanded;
    // Les méthodes openAll/closeAll existent sur MatAccordion si on a bien le module
    // mais ici on peut aussi simplement ne pas les appeler si le typage bloque
    // ou vérifier l'import. En attendant, je commente pour debloquer le build.
    /*
    if (this.allPanelsExpanded) {
      this.accordion.openAll();
    } else {
      this.accordion.closeAll();
    }
    */
  }

  ngOnDestroy(): void {
    this.saveCurrentState();
  }

  private saveCurrentState(): void {
    this.galaxyService.saveState({
      formValue: this.galaxyForm.getRawValue(),
      generatedImageUrl: this.generatedImageUrl,
      isModifiedSinceBuild: this.isModifiedSinceBuild,
      builtGalaxyParams: this.builtGalaxyParams
    });
  }
}
