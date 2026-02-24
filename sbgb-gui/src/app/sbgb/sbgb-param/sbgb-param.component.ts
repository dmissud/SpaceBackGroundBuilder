import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";

import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {selectCurrentSbgb, selectErrorMessage, selectImageBuild, selectInfoMessage} from "../state/sbgb.selectors";
import {Subscription} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Store} from "@ngrx/store";
import {ImageApiActions, SbgbPageActions} from "../state/sbgb.actions";
import {Actions, ofType} from "@ngrx/effects";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {MatExpansionModule} from "@angular/material/expansion";
import {Sbgb} from "../sbgb.model";

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

  infoMessageSub: Subscription | undefined;
  sbgbSub: Subscription | undefined;
  errorSub: Subscription | undefined;
  saveSuccessSub: Subscription | undefined;

  protected _myForm: FormGroup;
  private loadedFromDbSbgb: Sbgb | null = null;
  private builtSbgb: Sbgb | null = null;
  protected isModifiedSinceBuild: boolean = true;
  protected isBuilt: boolean = false;
  loadedSbgbId: string | null = null;
  currentNote: number = 0;
  readonly starValues = [1, 2, 3, 4, 5];

  constructor(private _snackBar: MatSnackBar, private store: Store, private actions$: Actions) {
    this._myForm = new FormGroup({
      [SbgbParamComponent.CONTROL_WIDTH]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_HEIGHT]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_SEED]: new FormControl('2569'),
      [SbgbParamComponent.CONTROL_OCTAVES]: new FormControl(1),
      [SbgbParamComponent.CONTROL_PERSISTENCE]: new FormControl(0.5),
      [SbgbParamComponent.CONTROL_LACUNARITY]: new FormControl(2.0),
      [SbgbParamComponent.CONTROL_SCALE]: new FormControl(100.0),
      [SbgbParamComponent.CONTROL_NOISE_TYPE]: new FormControl('FBM'),
      [SbgbParamComponent.CONTROL_PRESET]: new FormControl('CUSTOM'),
      [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: new FormControl(false),
      [SbgbParamComponent.CONTROL_ADVANCED_MODE]: new FormControl(false),
      // Layer controls
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
      [SbgbParamComponent.BACKGROUND_COLOR]: new FormControl('#000000'),
      [SbgbParamComponent.MIDDLE_COLOR]: new FormControl('#FFA500'),
      [SbgbParamComponent.FOREGROUND_COLOR]: new FormControl('#FFFFFF'),
      [SbgbParamComponent.BACK_THRESHOLD]: new FormControl('0.7'),
      [SbgbParamComponent.MIDDLE_THRESHOLD]: new FormControl('0.75'),
      [SbgbParamComponent.INTERPOLATION_TYPE]: new FormControl('LINEAR'),
      [SbgbParamComponent.TRANSPARENT_BACKGROUND]: new FormControl(false),
      [SbgbParamComponent.NAME]: new FormControl('')
    });

    this._myForm.valueChanges.subscribe(() => {
      this.isModifiedSinceBuild = true;
    });

    this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.valueChanges.subscribe(
      (backThreshold) => {
        let middleThreshold = this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {

          this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.setValue(backThreshold + 0.01);
        }
      }
    )
    this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.valueChanges.subscribe(
      (middleThreshold) => {
        let backThreshold = this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {

          this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.setValue(middleThreshold - 0.01);
        }
      }
    )
  }

  ngOnInit() {
    this.infoMessageSub = this.store.select(selectInfoMessage).subscribe((message: string) => {
      console.log(message);
      if (message && message.trim() !== "") {
        if (message === 'Image generated successfully') {
          this.isBuilt = true;
          this.isModifiedSinceBuild = false;
          this.builtSbgb = this.getSbgbFromForm();
          if (!this.loadedSbgbId) {
            this.currentNote = 0;
          }
        }
        this._snackBar.open(message, 'Close', { // Utilisation du message reçu en tant que texte pour le Snackbar
          duration: 3000,
          verticalPosition: 'top'
        });
        this.store.dispatch(SbgbPageActions.information({message: '', build: false}));
      }
    });
    this.sbgbSub = this.store.select(selectCurrentSbgb).subscribe((sbgb: Sbgb | null) => {
      if (sbgb && sbgb.id) {
        // Ne charger que si c'est une nouvelle image différente de celle déjà chargée
        // (pour éviter d'écraser loadedFromDbSbgb lors des builds)
        const isDifferentImage = !this.loadedFromDbSbgb || this.loadedFromDbSbgb.id !== sbgb.id;

        if (isDifferentImage) {
          this.loadedFromDbSbgb = sbgb;
          this.loadedSbgbId = sbgb.id ?? null;
          this.currentNote = sbgb.note ?? 0;
          this.isBuilt = false;
          this.builtSbgb = null;
          // Ne pas marquer comme modifié lors du chargement depuis la BDD
          // Le flag sera géré par les modifications du formulaire et le succès du build
          this._myForm.patchValue({
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
            [SbgbParamComponent.BACKGROUND_COLOR]: sbgb.imageColor.back,
            [SbgbParamComponent.MIDDLE_COLOR]: sbgb.imageColor.middle,
            [SbgbParamComponent.FOREGROUND_COLOR]: sbgb.imageColor.fore,
            [SbgbParamComponent.BACK_THRESHOLD]: sbgb.imageColor.backThreshold,
            [SbgbParamComponent.MIDDLE_THRESHOLD]: sbgb.imageColor.middleThreshold,
            [SbgbParamComponent.INTERPOLATION_TYPE]: sbgb.imageColor.interpolationType,
            [SbgbParamComponent.TRANSPARENT_BACKGROUND]: sbgb.imageColor.transparentBackground || false,
            [SbgbParamComponent.NAME]: sbgb.name || ''
          }, {emitEvent: false});
          // S'assurer que le flag reste à true jusqu'au build automatique
          this.isModifiedSinceBuild = true;
        }
      }
    });
    this.errorSub = this.store.select(selectErrorMessage).subscribe((message: string) => {
      if (message && message.trim() !== "") {
        this._snackBar.open(message, 'Close', {
          duration: 5000,
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });

    // Écouter le succès de la notation
    this.saveSuccessSub = this.actions$.pipe(
      ofType(ImageApiActions.imagesSaveSuccess, ImageApiActions.imagesSaveFail)
    ).subscribe((action) => {
      if (action.type === ImageApiActions.imagesSaveSuccess.type) {
        this._snackBar.open('Ciel étoilé sauvegardé avec succès', 'OK', {
          duration: 3000,
          verticalPosition: 'top'
        });
      } else if (action.type === ImageApiActions.imagesSaveFail.type) {
        const {message} = action as ReturnType<typeof ImageApiActions.imagesSaveFail>;
        console.error('Erreur lors de la sauvegarde:', message);
        this._snackBar.open(`Erreur lors de la sauvegarde: ${message}`, 'Fermer', {
          duration: 5000,
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });

    // Listen to preset changes in SBGB
    this._myForm.get(SbgbParamComponent.CONTROL_PRESET)?.valueChanges.subscribe(preset => {
      if (preset && preset !== 'CUSTOM') {
        this.applySbgbPreset(preset);
      }
    });
  }

  private applySbgbPreset(preset: string) {
    const defaultStructure = {
      octaves: 4,
      persistence: 0.5,
      lacunarity: 2.0,
      scale: 200,
      noiseType: 'FBM'
    };

    switch (preset) {
      case 'DEEP_SPACE':
        this._myForm.patchValue({
          [SbgbParamComponent.CONTROL_OCTAVES]: 6,
          [SbgbParamComponent.CONTROL_PERSISTENCE]: 0.5,
          [SbgbParamComponent.CONTROL_LACUNARITY]: 2.2,
          [SbgbParamComponent.CONTROL_SCALE]: 300,
          [SbgbParamComponent.BACKGROUND_COLOR]: '#000005',
          [SbgbParamComponent.MIDDLE_COLOR]: '#050515',
          [SbgbParamComponent.FOREGROUND_COLOR]: '#0a0a25',
          [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: true,
          layer2_enabled: true // Stars
        });
        break;
      case 'STARFIELD':
        this._myForm.patchValue({
          [SbgbParamComponent.CONTROL_OCTAVES]: 2,
          [SbgbParamComponent.CONTROL_SCALE]: 10,
          [SbgbParamComponent.BACKGROUND_COLOR]: '#000000',
          [SbgbParamComponent.CONTROL_USE_MULTI_LAYER]: true,
          layer0_enabled: false,
          layer1_enabled: false,
          layer2_enabled: true // Stars only
        });
        break;
      case 'NEBULA_DENSE':
        this._myForm.patchValue({
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
    if (this.infoMessageSub) {
      this.infoMessageSub.unsubscribe();
    }
    if (this.sbgbSub) {
      this.sbgbSub.unsubscribe();
    }
    if (this.errorSub) {
      this.errorSub.unsubscribe();
    }
    if (this.saveSuccessSub) {
      this.saveSuccessSub.unsubscribe();
    }
  }

  describeBase(): string {
    const f = this._myForm.value;
    return `${f.noiseType} ${f.octaves}oct — ${f.width}×${f.height} — seed ${f.seed}`;
  }

  describeCosmetic(): string {
    const f = this._myForm.value;
    const transparency = f.transparentBackground ? 'transparent' : 'opaque';
    return `${f.backgroundColor} → ${f.middleColor} → ${f.foregroundColor}, seuils ${Number(f.backThreshold).toFixed(2)}/${Number(f.middleThreshold).toFixed(2)}, ${transparency}`;
  }

  getParametersSummary(): string {
    const form = this._myForm.value;
    const noiseType = form.noiseType || 'FBM';
    const octaves = form.octaves || 4;
    const width = form.width || 2048;
    const height = form.height || 2048;
    const seed = form.seed || 0;
    const interpolation = form.interpolationType || 'LINEAR';
    const useMultiLayer = form.useMultiLayer || false;
    const preset = form.preset || 'CUSTOM';

    let summary = `${noiseType} noise with ${octaves} octaves`;

    if (useMultiLayer && preset !== 'CUSTOM') {
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
    const name = this._myForm.controls['name'].value || 'space-image';
    link.download = `${name}.png`;
    link.click();
  }

  private isModified(currentSbgb: Sbgb, referenceSbgb: Sbgb): boolean {
    if (!referenceSbgb) return true;

    const s1 = referenceSbgb.imageStructure;
    const s2 = currentSbgb.imageStructure;
    const c1 = referenceSbgb.imageColor;
    const c2 = currentSbgb.imageColor;

    return Number(s1.width) !== Number(s2.width) ||
      Number(s1.height) !== Number(s2.height) ||
      Number(s1.seed) !== Number(s2.seed) ||
      Number(s1.octaves) !== Number(s2.octaves) ||
      Number(s1.persistence) !== Number(s2.persistence) ||
      Number(s1.lacunarity) !== Number(s2.lacunarity) ||
      Number(s1.scale) !== Number(s2.scale) ||
      s1.noiseType !== s2.noiseType ||
      s1.preset !== s2.preset ||
      s1.useMultiLayer !== s2.useMultiLayer ||
      c1.back !== c2.back ||
      c1.middle !== c2.middle ||
      c1.fore !== c2.fore ||
      Number(c1.backThreshold) !== Number(c2.backThreshold) ||
      Number(c1.middleThreshold) !== Number(c2.middleThreshold) ||
      c1.interpolationType !== c2.interpolationType ||
      referenceSbgb.name !== currentSbgb.name;
    // Note: description is auto-generated, no need to compare
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

    const layers = useMultiLayerValue && this._myForm.get(SbgbParamComponent.CONTROL_ADVANCED_MODE)?.value
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
      {
        name: 'background',
        enabled: this._myForm.get('layer0_enabled')?.value,
        octaves: Number(this._myForm.get('layer0_octaves')?.value),
        persistence: Number(this._myForm.get('layer0_persistence')?.value),
        lacunarity: Number(this._myForm.get('layer0_lacunarity')?.value),
        scale: Number(this._myForm.get('layer0_scale')?.value),
        opacity: Number(this._myForm.get('layer0_opacity')?.value),
        blendMode: this._myForm.get('layer0_blendMode')?.value,
        noiseType: this._myForm.get('layer0_noiseType')?.value,
        seedOffset: Number(this._myForm.get('layer0_seedOffset')?.value)
      },
      {
        name: 'nebula',
        enabled: this._myForm.get('layer1_enabled')?.value,
        octaves: Number(this._myForm.get('layer1_octaves')?.value),
        persistence: Number(this._myForm.get('layer1_persistence')?.value),
        lacunarity: Number(this._myForm.get('layer1_lacunarity')?.value),
        scale: Number(this._myForm.get('layer1_scale')?.value),
        opacity: Number(this._myForm.get('layer1_opacity')?.value),
        blendMode: this._myForm.get('layer1_blendMode')?.value,
        noiseType: this._myForm.get('layer1_noiseType')?.value,
        seedOffset: Number(this._myForm.get('layer1_seedOffset')?.value)
      },
      {
        name: 'stars',
        enabled: this._myForm.get('layer2_enabled')?.value,
        octaves: Number(this._myForm.get('layer2_octaves')?.value),
        persistence: Number(this._myForm.get('layer2_persistence')?.value),
        lacunarity: Number(this._myForm.get('layer2_lacunarity')?.value),
        scale: Number(this._myForm.get('layer2_scale')?.value),
        opacity: Number(this._myForm.get('layer2_opacity')?.value),
        blendMode: this._myForm.get('layer2_blendMode')?.value,
        noiseType: this._myForm.get('layer2_noiseType')?.value,
        seedOffset: Number(this._myForm.get('layer2_seedOffset')?.value)
      }
    ];
  }

  private extractMetaFormValues() {
    let nameValue = this._myForm.controls[SbgbParamComponent.NAME].value;
    let descriptionValue = this.getParametersSummary(); // Auto-generate description
    return {nameValue, descriptionValue};
  }

  private extractImageFormValues() {
    let widthValue = this._myForm.controls[SbgbParamComponent.CONTROL_WIDTH].value;
    let heightValue = this._myForm.controls[SbgbParamComponent.CONTROL_HEIGHT].value;
    let seedValue = this._myForm.controls[SbgbParamComponent.CONTROL_SEED].value;
    let octavesValue = this._myForm.controls[SbgbParamComponent.CONTROL_OCTAVES].value;
    let persistenceValue = this._myForm.controls[SbgbParamComponent.CONTROL_PERSISTENCE].value;
    let lacunarityValue = this._myForm.controls[SbgbParamComponent.CONTROL_LACUNARITY].value;
    let scaleValue = this._myForm.controls[SbgbParamComponent.CONTROL_SCALE].value;
    let noiseTypeValue = this._myForm.controls[SbgbParamComponent.CONTROL_NOISE_TYPE].value;
    let presetValue = this._myForm.controls[SbgbParamComponent.CONTROL_PRESET].value;
    let useMultiLayerValue = this._myForm.controls[SbgbParamComponent.CONTROL_USE_MULTI_LAYER].value;
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
    let backgroundColorValue = this._myForm.controls[SbgbParamComponent.BACKGROUND_COLOR].value;
    let foregroundColorValue = this._myForm.controls[SbgbParamComponent.FOREGROUND_COLOR].value;
    let middleColorValue = this._myForm.controls[SbgbParamComponent.MIDDLE_COLOR].value;
    let backThresholdValue = this._myForm.controls[SbgbParamComponent.BACK_THRESHOLD].value;
    let middleThresholdValue = this._myForm.controls[SbgbParamComponent.MIDDLE_THRESHOLD].value;
    let interpolationTypeValue = this._myForm.controls[SbgbParamComponent.INTERPOLATION_TYPE].value;
    let transparentBackgroundValue = this._myForm.controls[SbgbParamComponent.TRANSPARENT_BACKGROUND].value;
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
