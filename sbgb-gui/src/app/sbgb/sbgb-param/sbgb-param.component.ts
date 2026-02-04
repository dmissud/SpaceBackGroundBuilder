import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {NgIf} from "@angular/common";
import {FormControl, FormGroup, ReactiveFormsModule} from "@angular/forms";
import {selectCurrentSbgb, selectErrorMessage, selectInfoMessage} from "../state/sbgb.selectors";
import {Subscription} from "rxjs";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Store} from "@ngrx/store";
import {ImageApiActions, SbgbPageActions} from "../state/sbgb.actions";
import {Actions, ofType} from "@ngrx/effects";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {Sbgb} from "../sbgb.model";

@Component({
  selector: 'app-sbgb-param',
  standalone: true,
  imports: [
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    MatSlider,
    MatSliderThumb,
    ReactiveFormsModule,
    MatTooltip,
    MatIcon,
    MatSuffix
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
  private static readonly BACKGROUND_COLOR = 'backgroundColor';
  private static readonly MIDDLE_COLOR = 'middleColor';
  private static readonly FOREGROUND_COLOR = 'foregroundColor';
  private static readonly BACK_THRESHOLD = 'backThreshold';
  private static readonly MIDDLE_THRESHOLD = 'middleThreshold';
  private static readonly NAME = 'name';
  private static readonly DESCRIPTION = 'description';

  infoMessageSub: Subscription | undefined;
  sbgbSub: Subscription | undefined;
  errorSub: Subscription | undefined;
  saveSuccessSub: Subscription | undefined;

  protected _myForm: FormGroup;
  private loadedFromDbSbgb: Sbgb | null = null;  // Image chargée depuis la BDD
  private builtSbgb: Sbgb | null = null;         // Image qui a été buildée
  protected isModifiedSinceBuild: boolean = true;
  protected isBuilt: boolean = false;

  constructor(private _snackBar: MatSnackBar, private store: Store, private actions$: Actions) {
    this._myForm = new FormGroup({
      [SbgbParamComponent.CONTROL_WIDTH]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_HEIGHT]: new FormControl('4000'),
      [SbgbParamComponent.CONTROL_SEED]: new FormControl('2569'),
      [SbgbParamComponent.CONTROL_OCTAVES]: new FormControl(1),
      [SbgbParamComponent.CONTROL_PERSISTENCE]: new FormControl(0.5),
      [SbgbParamComponent.CONTROL_LACUNARITY]: new FormControl(2.0),
      [SbgbParamComponent.CONTROL_SCALE]: new FormControl(100.0),
      [SbgbParamComponent.BACKGROUND_COLOR]: new FormControl('#000000'),
      [SbgbParamComponent.MIDDLE_COLOR]: new FormControl('#FFA500'),
      [SbgbParamComponent.FOREGROUND_COLOR]: new FormControl('#FFFFFF'),
      [SbgbParamComponent.BACK_THRESHOLD]: new FormControl('0.7'),
      [SbgbParamComponent.MIDDLE_THRESHOLD]: new FormControl('0.75'),
      [SbgbParamComponent.NAME]: new FormControl(''),
      [SbgbParamComponent.DESCRIPTION]: new FormControl(''),
    });

    this._myForm.valueChanges.subscribe(() => {
      this.isModifiedSinceBuild = true;
    });

    this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.valueChanges.subscribe(
      (backThreshold) => {
        let middleThreshold = this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {
          console.log(backThreshold);
          this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.setValue(backThreshold + 0.01);
        }
      }
    )
    this._myForm.get([SbgbParamComponent.MIDDLE_THRESHOLD])?.valueChanges.subscribe(
      (middleThreshold) => {
        let backThreshold = this._myForm.get([SbgbParamComponent.BACK_THRESHOLD])?.value;
        if (backThreshold >= middleThreshold) {
          console.log(middleThreshold);
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
          // Sauvegarder l'image qui vient d'être buildée
          this.builtSbgb = this.getSbgbFromForm();
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
          console.log('Chargement d\'une nouvelle image depuis la BDD:', sbgb.id);
          // Ne charger que les images qui ont un ID (venant de la BDD)
          this.loadedFromDbSbgb = sbgb;
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
            [SbgbParamComponent.BACKGROUND_COLOR]: sbgb.imageColor.back,
            [SbgbParamComponent.MIDDLE_COLOR]: sbgb.imageColor.middle,
            [SbgbParamComponent.FOREGROUND_COLOR]: sbgb.imageColor.fore,
            [SbgbParamComponent.BACK_THRESHOLD]: sbgb.imageColor.backThreshold,
            [SbgbParamComponent.MIDDLE_THRESHOLD]: sbgb.imageColor.middleThreshold,
            [SbgbParamComponent.NAME]: sbgb.name || '',
            [SbgbParamComponent.DESCRIPTION]: sbgb.description || '',
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

    // Écouter le succès de la sauvegarde
    this.saveSuccessSub = this.actions$.pipe(
      ofType(ImageApiActions.imagesSaveSuccess, ImageApiActions.imagesSaveFail)
    ).subscribe((action) => {
      if (action.type === ImageApiActions.imagesSaveSuccess.type) {
        const {sbgb} = action as ReturnType<typeof ImageApiActions.imagesSaveSuccess>;
        console.log('Image sauvegardée avec succès, mise à jour de loadedFromDbSbgb:', sbgb);
        // Mettre à jour la référence BDD avec l'image sauvegardée
        this.loadedFromDbSbgb = sbgb;
        // L'image buildée devient la référence BDD
        this.builtSbgb = sbgb;
        this._snackBar.open('Image sauvegardée avec succès', 'OK', {
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

  computeImage() {
    const sbgb = this.getSbgbFromForm();
    this.store.dispatch(SbgbPageActions.buildSbgb({sbgb, build: true}));
  }

  saveImage() {
    const sbgb = this.builtSbgb || this.getSbgbFromForm();

    // Si l'image a un ID (vient de la BDD), vérifier si elle a été modifiée
    if (this.loadedFromDbSbgb && this.loadedFromDbSbgb.id) {
      if (!this.isModified(sbgb, this.loadedFromDbSbgb)) {
        this._snackBar.open('L\'image n\'a pas été modifiée.', 'OK', {duration: 3000});
        return;
      }

      const isSameName = this.loadedFromDbSbgb.name === sbgb.name;
      const confirmMessage = isSameName
        ? `L'image "${sbgb.name}" existe déjà et a été modifiée. Voulez-vous la mettre à jour ?`
        : `L'image "${sbgb.name}" va être enregistrée. Voulez-vous continuer ?`;

      const confirmUpdate = confirm(confirmMessage);
      if (confirmUpdate) {
        this.store.dispatch(SbgbPageActions.saveSbgb({sbgb, forceUpdate: isSameName}));
      }
    } else {
      // Nouvelle image ou image buildée sans ID
      this.store.dispatch(SbgbPageActions.saveSbgb({sbgb, forceUpdate: false}));
    }
  }

  public hasUnsavedChanges(): boolean {
    const currentSbgb = this.getSbgbFromForm();
    if (!this.loadedFromDbSbgb) {
      // Pour une nouvelle image, on considère qu'il y a des changements si le nom n'est pas vide
      return !!currentSbgb.name;
    }
    return this.isModified(currentSbgb, this.loadedFromDbSbgb);
  }

  protected canBuild(): boolean {
    // Le bouton Build est actif si les paramètres ont été modifiés depuis le dernier build
    return this.isModifiedSinceBuild;
  }

  protected getBuildTooltip(): string {
    if (!this.isModifiedSinceBuild) {
      return 'Aucune modification détectée. Modifiez les paramètres pour pouvoir générer une nouvelle image.';
    }
    return 'Générer l\'image avec les paramètres actuels';
  }

  protected canSave(): boolean {
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

  protected getSaveTooltip(): string {
    if (!this.isBuilt) {
      return 'Vous devez d\'abord générer une image (Build) avant de pouvoir la sauvegarder.';
    }
    if (this.isModifiedSinceBuild) {
      return 'Vous avez modifié les paramètres. Générez l\'image (Build) avant de sauvegarder.';
    }
    if (!this.builtSbgb) {
      return 'Aucune image générée à sauvegarder.';
    }
    if (!this.loadedFromDbSbgb) {
      return 'Sauvegarder cette nouvelle image dans la bibliothèque';
    }
    if (this.isModified(this.builtSbgb, this.loadedFromDbSbgb)) {
      return 'Sauvegarder les modifications de cette image';
    }
    return 'L\'image n\'a pas été modifiée par rapport à celle en bibliothèque.';
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
      c1.back !== c2.back ||
      c1.middle !== c2.middle ||
      c1.fore !== c2.fore ||
      Number(c1.backThreshold) !== Number(c2.backThreshold) ||
      Number(c1.middleThreshold) !== Number(c2.middleThreshold) ||
      referenceSbgb.name !== currentSbgb.name ||
      referenceSbgb.description !== currentSbgb.description;
  }

  private getSbgbFromForm(): Sbgb {
    const {widthValue, heightValue, seedValue, octavesValue, persistenceValue, lacunarityValue, scaleValue} = this.extractImageFormValues();
    const {backgroundColorValue, middleColorValue, foregroundColorValue, backThresholdValue, middleThresholdValue}
      = this.extractColorFormValues();
    const {nameValue, descriptionValue} = this.extractMetaFormValues();
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
        scale: Number(scaleValue)
      },
      imageColor: {
        back: backgroundColorValue,
        middle: middleColorValue,
        fore: foregroundColorValue,
        backThreshold: Number(backThresholdValue),
        middleThreshold: Number(middleThresholdValue)
      }
    };
  }

  private extractMetaFormValues() {
    let nameValue = this._myForm.controls[SbgbParamComponent.NAME].value;
    let descriptionValue = this._myForm.controls[SbgbParamComponent.DESCRIPTION].value;
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
    return {widthValue, heightValue, seedValue, octavesValue, persistenceValue, lacunarityValue, scaleValue};
  }

  private extractColorFormValues() {
    let backgroundColorValue = this._myForm.controls[SbgbParamComponent.BACKGROUND_COLOR].value;
    let foregroundColorValue = this._myForm.controls[SbgbParamComponent.FOREGROUND_COLOR].value;
    let middleColorValue = this._myForm.controls[SbgbParamComponent.MIDDLE_COLOR].value;
    let backThresholdValue = this._myForm.controls[SbgbParamComponent.BACK_THRESHOLD].value;
    let middleThresholdValue = this._myForm.controls[SbgbParamComponent.MIDDLE_THRESHOLD].value;
    return {backgroundColorValue, foregroundColorValue, middleColorValue, backThresholdValue, middleThresholdValue};
  }

}
