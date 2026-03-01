import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";

import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {
  selectBases,
  selectCurrentSbgb,
  selectErrorMessage,
  selectImageBuild,
  selectInfoMessage,
  selectRenders
} from "../state/sbgb.selectors";
import {filter, Subject, take, takeUntil} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Store} from "@ngrx/store";
import {ImageApiActions, SbgbPageActions} from "../state/sbgb.actions";
import {Actions, ofType} from "@ngrx/effects";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {MatExpansionModule} from "@angular/material/expansion";
import {MatDialog} from "@angular/material/dialog";
import {
  SbgbStructuralChangeDialogComponent,
  StructuralChangeChoice
} from "../sbgb-structural-change-dialog/sbgb-structural-change-dialog.component";
import {NoiseBaseStructureDto, NoiseCosmeticRenderDto, Sbgb} from "../sbgb.model";
import {SbgbComparisonService} from "../sbgb-comparison.service";
import {INFO_MESSAGES, PresetName, STAR_RATING_VALUES} from "../sbgb.constants";

@Component({
  selector: 'app-sbgb-param',
  imports: [
    MatFormField,
    MatInput,
    MatLabel,
    MatSlider,
    MatSliderThumb,
    ReactiveFormsModule,
    MatTooltip,
    MatIcon,
    MatSuffix,
    MatExpansionModule
  ],
  templateUrl: './sbgb-param.component.html',
  styleUrl: './sbgb-param.component.scss'
})
export class SbgbParamComponent implements OnInit, OnDestroy {

  private static readonly CONTROL_WIDTH = 'width';
  private static readonly CONTROL_HEIGHT = 'height';
  private static readonly CONTROL_SEED = 'seed';
  private static readonly CONTROL_OCTAVES = 'octaves';
  private static readonly CONTROL_PERSISTENCE = 'persistence';
  private static readonly CONTROL_LACUNARITY = 'lacunarity';
  private static readonly CONTROL_SCALE = 'scale';
  private static readonly CONTROL_NOISE_TYPE = 'noiseType';
  private static readonly CONTROL_PRESET = 'preset';
  private static readonly CONTROL_USE_MULTI_LAYER = 'useMultiLayer';
  private static readonly CONTROL_ADVANCED_MODE = 'advancedMode';
  private static readonly BACKGROUND_COLOR = 'backgroundColor';
  private static readonly MIDDLE_COLOR = 'middleColor';
  private static readonly FOREGROUND_COLOR = 'foregroundColor';
  private static readonly BACK_THRESHOLD = 'backThreshold';
  private static readonly MIDDLE_THRESHOLD = 'middleThreshold';
  private static readonly INTERPOLATION_TYPE = 'interpolationType';
  private static readonly TRANSPARENT_BACKGROUND = 'transparentBackground';
  private static readonly NAME = 'name';
  private static readonly DESCRIPTION = 'description';

  private destroy$ = new Subject<void>();

  renders: NoiseCosmeticRenderDto[] = [];

  baseForm: FormGroup;
  cosmeticForm: FormGroup;
  protected sbgbForm: FormGroup;
  private baseFormSnapshot: any = null;
  private builtBaseFormSnapshot: any = null;
  private loadedFromDbSbgb: Sbgb | null = null;
  private builtSbgb: Sbgb | null = null;
  protected isModifiedSinceBuild: boolean = true;
  protected isBuilt: boolean = false;
  loadedSbgbId: string | null = null;
  currentNote: number = 0;
  private pendingAutoSelectBaseId: string | null = null;
  readonly starValues = STAR_RATING_VALUES;

  constructor(private _snackBar: MatSnackBar, private store: Store, private actions$: Actions, private dialog: MatDialog, private sbgbComparison: SbgbComparisonService) {
    this.baseForm = new FormGroup({
      [SbgbParamComponent.CONTROL_WIDTH]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_HEIGHT]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_SEED]: new FormControl('2569'),
      [SbgbParamComponent.CONTROL_OCTAVES]: new FormControl(1),
      [SbgbParamComponent.CONTROL_PERSISTENCE]: new FormControl(0.5),
      [SbgbParamComponent.CONTROL_LACUNARITY]: new FormControl(2.0),
      [SbgbParamComponent.CONTROL_SCALE]: new FormControl(100.0),
      [SbgbParamComponent.CONTROL_NOISE_TYPE]: new FormControl('FBM'),
      [SbgbParamComponent.CONTROL_PRESET]: new FormControl(PresetName.CUSTOM),
      [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: new FormControl(false),
      [SbgbParamComponent.CONTROL_ADVANCED_MODE]: new FormControl(false),
      layer0_enabled: new FormControl(true),
      layer0_octaves: new FormControl(3),
      layer0_persistence: new FormControl(0.5),
      layer0_lacunarity: new FormControl(2.0),
      layer0_scale: new FormControl(150.0),
      layer0_opacity: new FormControl(1.0),
      layer0_blendMode: new FormControl('NORMAL'),
      layer0_noiseType: new FormControl('FBM'),
      layer0_seedOffset: new FormControl(0),
      layer1_enabled: new FormControl(true),
      layer1_octaves: new FormControl(5),
      layer1_persistence: new FormControl(0.6),
      layer1_lacunarity: new FormControl(2.2),
      layer1_scale: new FormControl(80.0),
      layer1_opacity: new FormControl(0.7),
      layer1_blendMode: new FormControl('OVERLAY'),
      layer1_noiseType: new FormControl('FBM'),
      layer1_seedOffset: new FormControl(1000),
      layer2_enabled: new FormControl(true),
      layer2_octaves: new FormControl(1),
      layer2_persistence: new FormControl(0.3),
      layer2_lacunarity: new FormControl(2.0),
      layer2_scale: new FormControl(50.0),
      layer2_opacity: new FormControl(0.9),
      layer2_blendMode: new FormControl('SCREEN'),
      layer2_noiseType: new FormControl('FBM'),
      layer2_seedOffset: new FormControl(2000),
      [SbgbParamComponent.NAME]: new FormControl('')
    });

    this.cosmeticForm = new FormGroup({
      [SbgbParamComponent.BACKGROUND_COLOR]: new FormControl('#000000'),
      [SbgbParamComponent.MIDDLE_COLOR]: new FormControl('#FFA500'),
      [SbgbParamComponent.FOREGROUND_COLOR]: new FormControl('#FFFFFF'),
      [SbgbParamComponent.BACK_THRESHOLD]: new FormControl('0.7'),
      [SbgbParamComponent.MIDDLE_THRESHOLD]: new FormControl('0.75'),
      [SbgbParamComponent.INTERPOLATION_TYPE]: new FormControl('LINEAR'),
      [SbgbParamComponent.TRANSPARENT_BACKGROUND]: new FormControl(false),
    });

    this.sbgbForm = new FormGroup({
      base: this.baseForm,
      cosmetic: this.cosmeticForm
    });

    this.sbgbForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.isModifiedSinceBuild = true;
    });

    this.cosmeticForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.store.dispatch(SbgbPageActions.clearSelectedRender());
    });

    this.setupThresholdSync();
  }

  ngOnInit() {
    this.setupInfoMessageListener();
    this.setupSbgbLoader();
    this.setupErrorMessageListener();
    this.setupSaveResultListener();
    this.setupRendersLoader();
    this.setupBaseAutoSelect();
    this.setupRenderCosmeticsLoader();

    this.baseFormSnapshot = this.baseForm.value;
    this.baseForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(newValue => {
      this.baseFormSnapshot = newValue;
    });

    this.baseForm.get(SbgbParamComponent.CONTROL_PRESET)?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(preset => {
      if (preset && preset !== PresetName.CUSTOM) {
        this.applySbgbPreset(preset);
      }
    });
  }

  private applySbgbPreset(preset: string) {
    switch (preset) {
      case PresetName.DEEP_SPACE:
        this.baseForm.patchValue({
          [SbgbParamComponent.CONTROL_OCTAVES]: 6,
          [SbgbParamComponent.CONTROL_PERSISTENCE]: 0.5,
          [SbgbParamComponent.CONTROL_LACUNARITY]: 2.2,
          [SbgbParamComponent.CONTROL_SCALE]: 300,
          [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: true,
          layer2_enabled: true
        });
        this.cosmeticForm.patchValue({
          [SbgbParamComponent.BACKGROUND_COLOR]: '#000005',
          [SbgbParamComponent.MIDDLE_COLOR]: '#050515',
          [SbgbParamComponent.FOREGROUND_COLOR]: '#0a0a25',
        });
        break;
      case PresetName.STARFIELD:
        this.baseForm.patchValue({
          [SbgbParamComponent.CONTROL_OCTAVES]: 2,
          [SbgbParamComponent.CONTROL_SCALE]: 10,
          [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: true,
          layer0_enabled: false,
          layer1_enabled: false,
          layer2_enabled: true
        });
        this.cosmeticForm.patchValue({
          [SbgbParamComponent.BACKGROUND_COLOR]: '#000000',
        });
        break;
      case PresetName.NEBULA_DENSE:
        this.baseForm.patchValue({
          [SbgbParamComponent.CONTROL_OCTAVES]: 8,
          [SbgbParamComponent.CONTROL_PERSISTENCE]: 0.7,
          [SbgbParamComponent.CONTROL_SCALE]: 150,
          [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: true,
          layer1_enabled: true,
          layer2_enabled: true
        });
        break;
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  reapplyRendersWithNewBase(): void {
    const currentSbgb = this.getSbgbFromForm();
    this.renders.forEach(render => {
      const sbgbWithRenderCosmetic: Sbgb = {
        ...currentSbgb,
        imageColor: {
          back: render.back,
          middle: render.middle,
          fore: render.fore,
          backThreshold: render.backThreshold,
          middleThreshold: render.middleThreshold,
          interpolationType: render.interpolationType,
          transparentBackground: render.transparentBackground
        }
      };
      this.store.dispatch(SbgbPageActions.rateSbgb({sbgb: sbgbWithRenderCosmetic, note: render.note}));
    });
  }

  private setupThresholdSync(): void {
    this.cosmeticForm.get(SbgbParamComponent.BACK_THRESHOLD)?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(
      (backThreshold) => {
        const middleThreshold = this.cosmeticForm.get(SbgbParamComponent.MIDDLE_THRESHOLD)?.value;
        if (backThreshold >= middleThreshold) {
          this.cosmeticForm.get(SbgbParamComponent.MIDDLE_THRESHOLD)?.setValue(backThreshold + 0.01);
        }
      }
    );
    this.cosmeticForm.get(SbgbParamComponent.MIDDLE_THRESHOLD)?.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(
      (middleThreshold) => {
        const backThreshold = this.cosmeticForm.get(SbgbParamComponent.BACK_THRESHOLD)?.value;
        if (backThreshold >= middleThreshold) {
          this.cosmeticForm.get(SbgbParamComponent.BACK_THRESHOLD)?.setValue(middleThreshold - 0.01);
        }
      }
    );
  }

  private setupInfoMessageListener(): void {
    this.store.select(selectInfoMessage).pipe(takeUntil(this.destroy$)).subscribe((message: string) => {
      if (!message || message.trim() === '') return;
      if (message === INFO_MESSAGES.IMAGE_GENERATED) {
        this.isBuilt = true;
        this.isModifiedSinceBuild = false;
        this.builtSbgb = this.getSbgbFromForm();
        this.builtBaseFormSnapshot = this.baseForm.value;
        if (!this.loadedSbgbId) {
          this.currentNote = 0;
        }
      }
      this._snackBar.open(message, 'Close', {duration: 3000, verticalPosition: 'top'});
      this.store.dispatch(SbgbPageActions.information({message: '', build: false}));
    });
  }

  private setupSbgbLoader(): void {
    this.store.select(selectCurrentSbgb).pipe(takeUntil(this.destroy$)).subscribe((sbgb: Sbgb | null) => {
      if (!sbgb?.id) return;
      const isDifferentSbgb = !this.loadedFromDbSbgb || this.loadedFromDbSbgb.id !== sbgb.id;
      if (isDifferentSbgb) {
        this.loadedFromDbSbgb = sbgb;
        this.loadedSbgbId = sbgb.id ?? null;
        this.isBuilt = false;
        this.builtSbgb = null;
        this.currentNote = sbgb.note ?? 0;
        this.store.dispatch(SbgbPageActions.loadRendersForBase({baseId: sbgb.id!}));
        this.loadFormValuesFromSbgb(sbgb);
        this.isModifiedSinceBuild = true;
      }
    });
  }

  private setupErrorMessageListener(): void {
    this.store.select(selectErrorMessage).pipe(takeUntil(this.destroy$)).subscribe((message: string) => {
      if (message && message.trim() !== '') {
        this._snackBar.open(message, 'Close', {duration: 5000, verticalPosition: 'top', panelClass: ['error-snackbar']});
      }
    });
  }

  private setupSaveResultListener(): void {
    this.actions$.pipe(
      ofType(ImageApiActions.imagesSaveSuccess, ImageApiActions.imagesSaveFail),
      takeUntil(this.destroy$)
    ).subscribe((action) => {
      if (action.type === ImageApiActions.imagesSaveSuccess.type) {
        this._snackBar.open(INFO_MESSAGES.RENDER_SAVED, 'OK', {duration: 3000, verticalPosition: 'top'});
      } else {
        const {message} = action as ReturnType<typeof ImageApiActions.imagesSaveFail>;
        this._snackBar.open(`${INFO_MESSAGES.SAVE_ERROR_PREFIX}${message}`, 'Fermer', {
          duration: 5000, verticalPosition: 'top', panelClass: ['error-snackbar']
        });
      }
    });
  }

  private setupRendersLoader(): void {
    this.store.select(selectRenders).pipe(takeUntil(this.destroy$)).subscribe((renders: NoiseCosmeticRenderDto[]) => {
      this.renders = renders;
      this.autoSelectBestRenderIfPending(renders);
    });
  }

  private setupRenderCosmeticsLoader(): void {
    this.actions$.pipe(
      ofType(SbgbPageActions.applyRenderCosmetics),
      takeUntil(this.destroy$)
    ).subscribe(({render}) => this.loadRenderCosmetics(render));
  }

  private setupBaseAutoSelect(): void {
    this.store.select(selectBases).pipe(
      filter(bases => bases.length > 0),
      take(1),
      takeUntil(this.destroy$)
    ).subscribe(bases => {
      const matchingBase = this.findBaseMatchingDefaultParams(bases);
      if (matchingBase) {
        this.pendingAutoSelectBaseId = matchingBase.id;
        this.store.dispatch(SbgbPageActions.loadRendersForBase({baseId: matchingBase.id}));
      }
    });
  }

  private loadFormValuesFromSbgb(sbgb: Sbgb): void {
    this.baseForm.patchValue({
      [SbgbParamComponent.CONTROL_WIDTH]: sbgb.imageStructure.width,
      [SbgbParamComponent.CONTROL_HEIGHT]: sbgb.imageStructure.height,
      [SbgbParamComponent.CONTROL_SEED]: sbgb.imageStructure.seed,
      [SbgbParamComponent.CONTROL_OCTAVES]: sbgb.imageStructure.octaves,
      [SbgbParamComponent.CONTROL_PERSISTENCE]: sbgb.imageStructure.persistence,
      [SbgbParamComponent.CONTROL_LACUNARITY]: sbgb.imageStructure.lacunarity,
      [SbgbParamComponent.CONTROL_SCALE]: sbgb.imageStructure.scale,
      [SbgbParamComponent.CONTROL_NOISE_TYPE]: sbgb.imageStructure.noiseType || 'FBM',
      [SbgbParamComponent.CONTROL_PRESET]: sbgb.imageStructure.preset,
      [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: sbgb.imageStructure.useMultiLayer,
      [SbgbParamComponent.NAME]: sbgb.name || ''
    }, {emitEvent: false});
    this.cosmeticForm.patchValue({
      [SbgbParamComponent.BACKGROUND_COLOR]: sbgb.imageColor.back,
      [SbgbParamComponent.MIDDLE_COLOR]: sbgb.imageColor.middle,
      [SbgbParamComponent.FOREGROUND_COLOR]: sbgb.imageColor.fore,
      [SbgbParamComponent.BACK_THRESHOLD]: sbgb.imageColor.backThreshold,
      [SbgbParamComponent.MIDDLE_THRESHOLD]: sbgb.imageColor.middleThreshold,
      [SbgbParamComponent.INTERPOLATION_TYPE]: sbgb.imageColor.interpolationType,
      [SbgbParamComponent.TRANSPARENT_BACKGROUND]: sbgb.imageColor.transparentBackground || false,
    }, {emitEvent: false});
  }

  deleteRenderById(renderId: string): void {
    this.store.dispatch(SbgbPageActions.deleteRender({renderId}));
  }

  loadRendersForBaseId(baseId: string): void {
    this.store.dispatch(SbgbPageActions.loadRendersForBase({baseId}));
  }

  private loadRenderCosmetics(render: NoiseCosmeticRenderDto): void {
    this.cosmeticForm.patchValue({
      [SbgbParamComponent.BACKGROUND_COLOR]: render.back,
      [SbgbParamComponent.MIDDLE_COLOR]: render.middle,
      [SbgbParamComponent.FOREGROUND_COLOR]: render.fore,
      [SbgbParamComponent.BACK_THRESHOLD]: render.backThreshold,
      [SbgbParamComponent.MIDDLE_THRESHOLD]: render.middleThreshold,
      [SbgbParamComponent.INTERPOLATION_TYPE]: render.interpolationType,
      [SbgbParamComponent.TRANSPARENT_BACKGROUND]: render.transparentBackground,
    }, {emitEvent: false});
    this.currentNote = render.note;
    this.isModifiedSinceBuild = true;
    this.isBuilt = false;
  }

  describeBase(): string {
    const formValues = this.baseForm.value;
    return `${formValues.noiseType} ${formValues.octaves}oct — ${formValues.width}×${formValues.height} — seed ${formValues.seed}`;
  }

  describeCosmetic(): string {
    const formValues = this.cosmeticForm.value;
    const transparency = formValues.transparentBackground ? 'transparent' : 'opaque';
    return `${formValues.backgroundColor} → ${formValues.middleColor} → ${formValues.foregroundColor}, seuils ${Number(formValues.backThreshold).toFixed(2)}/${Number(formValues.middleThreshold).toFixed(2)}, ${transparency}`;
  }

  getParametersSummary(): string {
    const form = this.baseForm.value;
    const noiseType = form.noiseType || 'FBM';
    const octaves = form.octaves || 4;
    const width = form.width || 2048;
    const height = form.height || 2048;
    const seed = form.seed || 0;
    const interpolation = form.interpolationType || 'LINEAR';
    const useMultiLayer = form.useMultiLayer || false;
    const preset = form.preset || PresetName.CUSTOM;

    let summary = `${noiseType} noise with ${octaves} octaves`;

    if (useMultiLayer && preset !== PresetName.CUSTOM) {
      summary += `, ${preset} preset`;
    } else if (useMultiLayer) {
      summary += `, multi-layer enabled`;
    }

    summary += `, ${width}x${height}px`;
    summary += `, ${interpolation} interpolation`;
    summary += `, seed ${seed}`;

    return summary;
  }

  computeImage() {
    const hasStructuralChange = this.builtBaseFormSnapshot !== null &&
      JSON.stringify(this.baseForm.value) !== JSON.stringify(this.builtBaseFormSnapshot);

    if (this.renders.length > 0 && hasStructuralChange) {
      this.dialog.open(SbgbStructuralChangeDialogComponent, {
        data: {rendersCount: this.renders.length}
      }).afterClosed().subscribe((choice: StructuralChangeChoice | undefined) => {
        if (choice === StructuralChangeChoice.NEW_BASE) {
          this.store.dispatch(SbgbPageActions.clearRenders());
          this.dispatchBuild();
        } else if (choice === StructuralChangeChoice.CLEAR) {
          this.renders.forEach(r => this.store.dispatch(SbgbPageActions.deleteRender({renderId: r.id})));
          this.dispatchBuild();
        } else if (choice === StructuralChangeChoice.REAPPLY) {
          this.reapplyRendersWithNewBase();
          this.dispatchBuild();
        }
      });
    } else {
      this.dispatchBuild();
    }
  }

  private dispatchBuild(): void {
    const sbgb = this.getSbgbFromForm();
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }

  onNoteSelected(note: number): void {
    if (note < 1) return;
    this.currentNote = note;
    const sbgb = this.builtSbgb || this.getSbgbFromForm();
    this.store.dispatch(SbgbPageActions.rateSbgb({sbgb, note}));
  }

  canRate(): boolean {
    return this.isBuilt && !this.isModifiedSinceBuild;
  }

  getRatingTooltip(): string {
    if (!this.isBuilt) return 'Générez d\'abord un ciel étoilé avant de pouvoir le noter et le sauvegarder.';
    if (this.isModifiedSinceBuild) return 'Regénérez le ciel étoilé avant de noter.';
    return 'Attribuez une note pour sauvegarder ce ciel étoilé';
  }

  public hasUnsavedChanges(): boolean {
    const currentSbgb = this.getSbgbFromForm();
    if (!this.loadedFromDbSbgb) {
      // Pour une nouvelle image, on considère qu'il y a des changements si le nom n'est pas vide
      return !!currentSbgb.name;
    }
    return this.isModified(currentSbgb, this.loadedFromDbSbgb);
  }

  canBuild(): boolean {
    // Le bouton Build est actif si les paramètres ont été modifiés depuis le dernier build
    return this.isModifiedSinceBuild;
  }

  getBuildTooltip(): string {
    if (!this.isModifiedSinceBuild) {
      return 'Aucune modification détectée. Modifiez les paramètres pour pouvoir générer un nouveau ciel étoilé.';
    }
    return 'Générer le ciel étoilé avec les paramètres actuels';
  }

  canSave(): boolean {
    // Le bouton Save est actif si :
    // 1. Une image a été buildée (isBuilt = true)
    // 2. Le formulaire n'a pas été modifié depuis le build (isModifiedSinceBuild = false)
    // 3. ET :
    //    - Soit c'est une nouvelle image (!loadedFromDbSbgb)
    //    - Soit l'image buildée est différente de celle chargée depuis la BDD

    if (!this.isBuilt || this.isModifiedSinceBuild || !this.builtSbgb) {
      return false;
    }

    // Nouvelle image : pas de référence en BDD
    if (!this.loadedFromDbSbgb) {
      return true;
    }

    // Image modifiée : comparer builtSbgb avec loadedFromDbSbgb
    return this.isModified(this.builtSbgb, this.loadedFromDbSbgb);
  }

  getSaveTooltip(): string {
    if (!this.isBuilt) {
      return 'Vous devez d\'abord générer un ciel étoilé (Build) avant de pouvoir le sauvegarder.';
    }
    if (this.isModifiedSinceBuild) {
      return 'Vous avez modifié les paramètres. Générez le ciel étoilé (Build) avant de sauvegarder.';
    }
    if (!this.builtSbgb) {
      return 'Aucun ciel étoilé généré à sauvegarder.';
    }
    if (!this.loadedFromDbSbgb) {
      return 'Sauvegarder ce nouveau ciel étoilé dans la bibliothèque';
    }
    if (this.isModified(this.builtSbgb, this.loadedFromDbSbgb)) {
      return 'Sauvegarder les modifications de ce ciel étoilé';
    }
    return 'Le ciel étoilé n\'a pas été modifié par rapport à celui en bibliothèque.';
  }

  canDownload(): boolean {
    return this.isBuilt && !this.isModifiedSinceBuild;
  }

  getDownloadTooltip(): string {
    if (!this.isBuilt) {
      return 'Vous devez d\'abord générer un ciel étoilé (Build) avant de pouvoir le télécharger.';
    }
    if (this.isModifiedSinceBuild) {
      return 'Vous avez modifié les paramètres. Générez le ciel étoilé (Build) avant de télécharger.';
    }
    return 'Télécharger le ciel étoilé généré sur votre PC';
  }

  downloadImage() {
    const image = this.store.selectSignal(selectImageBuild)();
    if (!image) return;

    const link = document.createElement('a');
    link.href = image as string;
    const name = this.baseForm.controls['name'].value || 'space-image';
    link.download = `${name}.png`;
    link.click();
  }

  private isModified(currentSbgb: Sbgb, referenceSbgb: Sbgb): boolean {
    return this.sbgbComparison.isModified(currentSbgb, referenceSbgb);
  }

  private getSbgbFromForm(): Sbgb {
    const {
      widthValue,
      heightValue,
      seedValue,
      octavesValue,
      persistenceValue,
      lacunarityValue,
      scaleValue,
      noiseTypeValue,
      presetValue,
      useMultiLayerValue
    } = this.extractImageFormValues();
    const {
      backgroundColorValue,
      middleColorValue,
      foregroundColorValue,
      backThresholdValue,
      middleThresholdValue,
      interpolationTypeValue,
      transparentBackgroundValue
    }
      = this.extractColorFormValues();
    const {nameValue, descriptionValue} = this.extractMetaFormValues();

    const layers = useMultiLayerValue && this.baseForm.get(SbgbParamComponent.CONTROL_ADVANCED_MODE)?.value
      ? this.extractLayersFromForm()
      : undefined;

    return {
      id: this.loadedFromDbSbgb?.id,
      name: nameValue,
      description: descriptionValue,
      imageStructure: {
        width: Number(widthValue),
        height: Number(heightValue),
        seed: Number(seedValue),
        octaves: Number(octavesValue),
        persistence: Number(persistenceValue),
        lacunarity: Number(lacunarityValue),
        scale: Number(scaleValue),
        noiseType: noiseTypeValue,
        preset: presetValue,
        useMultiLayer: useMultiLayerValue,
        layers: layers
      },
      imageColor: {
        back: backgroundColorValue,
        middle: middleColorValue,
        fore: foregroundColorValue,
        backThreshold: Number(backThresholdValue),
        middleThreshold: Number(middleThresholdValue),
        interpolationType: interpolationTypeValue,
        transparentBackground: transparentBackgroundValue
      }
    };
  }

  private extractLayersFromForm() {
    return [
      this.extractLayerConfig('0', 'background'),
      this.extractLayerConfig('1', 'nebula'),
      this.extractLayerConfig('2', 'stars')
    ];
  }

  private findBaseMatchingDefaultParams(bases: NoiseBaseStructureDto[]): NoiseBaseStructureDto | undefined {
    const f = this.baseForm.value;
    return bases.find(b =>
      b.width === Number(f.width) &&
      b.height === Number(f.height) &&
      b.seed === Number(f.seed) &&
      b.octaves === Number(f.octaves) &&
      b.persistence === Number(f.persistence) &&
      b.lacunarity === Number(f.lacunarity) &&
      b.scale === Number(f.scale) &&
      b.noiseType === f.noiseType &&
      b.useMultiLayer === f.useMultiLayer
    );
  }

  private autoSelectBestRenderIfPending(renders: NoiseCosmeticRenderDto[]): void {
    if (!this.pendingAutoSelectBaseId) return;
    if (renders.length === 0) return;
    if (renders[0].baseStructureId !== this.pendingAutoSelectBaseId) return;

    const bestRender = [...renders].sort((a, b) => b.note - a.note)[0];
    this.pendingAutoSelectBaseId = null;
    this.store.dispatch(SbgbPageActions.selectRender({renderId: bestRender.id}));
    this.loadRenderCosmetics(bestRender);
  }

  private extractLayerConfig(index: string, name: string) {
    const p = (field: string) => this.sbgbForm.get(`layer${index}_${field}`)?.value;
    return {
      name,
      enabled: p('enabled'),
      octaves: Number(p('octaves')),
      persistence: Number(p('persistence')),
      lacunarity: Number(p('lacunarity')),
      scale: Number(p('scale')),
      opacity: Number(p('opacity')),
      blendMode: p('blendMode'),
      noiseType: p('noiseType'),
      seedOffset: Number(p('seedOffset'))
    };
  }

  private extractMetaFormValues() {
    let nameValue = this.baseForm.controls[SbgbParamComponent.NAME].value;
    let descriptionValue = this.getParametersSummary(); // Auto-generate description
    return {nameValue, descriptionValue};
  }

  private extractImageFormValues() {
    let widthValue = this.baseForm.controls[SbgbParamComponent.CONTROL_WIDTH].value;
    let heightValue = this.baseForm.controls[SbgbParamComponent.CONTROL_HEIGHT].value;
    let seedValue = this.baseForm.controls[SbgbParamComponent.CONTROL_SEED].value;
    let octavesValue = this.baseForm.controls[SbgbParamComponent.CONTROL_OCTAVES].value;
    let persistenceValue = this.baseForm.controls[SbgbParamComponent.CONTROL_PERSISTENCE].value;
    let lacunarityValue = this.baseForm.controls[SbgbParamComponent.CONTROL_LACUNARITY].value;
    let scaleValue = this.baseForm.controls[SbgbParamComponent.CONTROL_SCALE].value;
    let noiseTypeValue = this.baseForm.controls[SbgbParamComponent.CONTROL_NOISE_TYPE].value;
    let presetValue = this.baseForm.controls[SbgbParamComponent.CONTROL_PRESET].value;
    let useMultiLayerValue = this.baseForm.controls[SbgbParamComponent.CONTROL_USE_MULTI_LAYER].value;
    return {
      widthValue,
      heightValue,
      seedValue,
      octavesValue,
      persistenceValue,
      lacunarityValue,
      scaleValue,
      noiseTypeValue,
      presetValue,
      useMultiLayerValue
    };
  }

  private extractColorFormValues() {
    let backgroundColorValue = this.cosmeticForm.controls[SbgbParamComponent.BACKGROUND_COLOR].value;
    let foregroundColorValue = this.cosmeticForm.controls[SbgbParamComponent.FOREGROUND_COLOR].value;
    let middleColorValue = this.cosmeticForm.controls[SbgbParamComponent.MIDDLE_COLOR].value;
    let backThresholdValue = this.cosmeticForm.controls[SbgbParamComponent.BACK_THRESHOLD].value;
    let middleThresholdValue = this.cosmeticForm.controls[SbgbParamComponent.MIDDLE_THRESHOLD].value;
    let interpolationTypeValue = this.cosmeticForm.controls[SbgbParamComponent.INTERPOLATION_TYPE].value;
    let transparentBackgroundValue = this.cosmeticForm.controls[SbgbParamComponent.TRANSPARENT_BACKGROUND].value;
    return {
      backgroundColorValue,
      foregroundColorValue,
      middleColorValue,
      backThresholdValue,
      middleThresholdValue,
      interpolationTypeValue,
      transparentBackgroundValue
    };
  }

}
