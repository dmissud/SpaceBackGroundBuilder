import {Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {GalaxyService} from "../galaxy.service";
import {GalaxyRequestCmd} from "../galaxy.model";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-galaxy-param',
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
    MatSuffix,
    NgIf
  ],
  templateUrl: './galaxy-param.component.html',
  styleUrl: './galaxy-param.component.scss'
})
export class GalaxyParamComponent implements OnInit {

  galaxyForm!: FormGroup;
  generatedImageUrl: string | null = null;
  isGenerating = false;

  constructor(
    private galaxyService: GalaxyService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.galaxyForm = new FormGroup({
      name: new FormControl('Galaxy', [Validators.required]),
      description: new FormControl('Generated spiral galaxy'),
      width: new FormControl(4000, [Validators.required, Validators.min(100)]),
      height: new FormControl(4000, [Validators.required, Validators.min(100)]),
      seed: new FormControl(Math.floor(Math.random() * 1000000)),
      numberOfArms: new FormControl(2, [Validators.required, Validators.min(1)]),
      armWidth: new FormControl(80, [Validators.required, Validators.min(10)]),
      armRotation: new FormControl(4, [Validators.required]),
      coreSize: new FormControl(0.05, [Validators.required]),
      galaxyRadius: new FormControl(1500, [Validators.required]),
      noiseOctaves: new FormControl(4, [Validators.required]),
      noisePersistence: new FormControl(0.5, [Validators.required]),
      noiseLacunarity: new FormControl(2.0, [Validators.required]),
      noiseScale: new FormControl(200, [Validators.required]),
      spaceBackgroundColor: new FormControl('#050510'),
      coreColor: new FormControl('#FFFADC'),
      armColor: new FormControl('#B4C8FF'),
      outerColor: new FormControl('#3C5078')
    });
  }

  generateGalaxy(): void {
    if (this.galaxyForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', {duration: 3000});
      return;
    }

    this.isGenerating = true;
    const request: GalaxyRequestCmd = this.galaxyForm.value;

    this.galaxyService.buildGalaxy(request).subscribe({
      next: (blob) => {
        this.generatedImageUrl = URL.createObjectURL(blob);
        this.isGenerating = false;
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

    this.isGenerating = true;
    const request: GalaxyRequestCmd = this.galaxyForm.value;

    this.galaxyService.createGalaxy(request).subscribe({
      next: (galaxy) => {
        this.isGenerating = false;
        this.snackBar.open(`Galaxy "${galaxy.name}" saved successfully!`, 'Close', {duration: 3000});
      },
      error: (error) => {
        console.error('Error saving galaxy:', error);
        this.snackBar.open('Error saving galaxy', 'Close', {duration: 3000});
        this.isGenerating = false;
      }
    });
  }

  randomizeSeed(): void {
    this.galaxyForm.patchValue({
      seed: Math.floor(Math.random() * 1000000)
    });
  }

  canDownload(): boolean {
    return !!this.generatedImageUrl && !this.isGenerating;
  }

  getDownloadTooltip(): string {
    if (this.isGenerating) {
      return 'Generation en cours, veuillez patienter.';
    }
    if (!this.generatedImageUrl) {
      return 'Vous devez d\'abord generer une image (Generate Preview) avant de pouvoir la telecharger.';
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

  loadPreset(preset: string): void {
    switch (preset) {
      case 'CLASSIC':
        this.galaxyForm.patchValue({
          numberOfArms: 2,
          armWidth: 80,
          armRotation: 4,
          coreSize: 0.05,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'BARRED':
        this.galaxyForm.patchValue({
          numberOfArms: 2,
          armWidth: 100,
          armRotation: 3,
          coreSize: 0.08,
          galaxyRadius: 1500,
          noiseOctaves: 5,
          noisePersistence: 0.6,
          noiseLacunarity: 2.2,
          noiseScale: 150
        });
        break;
      case 'MULTI_ARM':
        this.galaxyForm.patchValue({
          numberOfArms: 3,
          armWidth: 70,
          armRotation: 5,
          coreSize: 0.04,
          galaxyRadius: 1500,
          noiseOctaves: 6,
          noisePersistence: 0.55,
          noiseLacunarity: 2.1,
          noiseScale: 180
        });
        break;
    }
  }
}
