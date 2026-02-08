import {Component, OnInit} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel, MatSuffix} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatOption, MatSelect} from "@angular/material/select";
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {GalaxyService} from "../galaxy.service";
import {GalaxyImageDTO, GalaxyRequestCmd} from "../galaxy.model";


@Component({
    selector: 'app-galaxy-param',
    imports: [
    MatButton,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    MatTooltip,
    MatIcon,
    MatSuffix,
    MatSelect,
    MatOption,
    MatProgressSpinner
],
    templateUrl: './galaxy-param.component.html',
    styleUrl: './galaxy-param.component.scss'
})
export class GalaxyParamComponent implements OnInit {

  galaxyForm!: FormGroup;
  generatedImageUrl: string | null = null;
  isGenerating = false;
  loadedGalaxyName: string | null = null;

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
      galaxyType: new FormControl('SPIRAL'),
      // Spiral structure parameters
      numberOfArms: new FormControl(2, [Validators.required, Validators.min(1)]),
      armWidth: new FormControl(80, [Validators.required, Validators.min(10)]),
      armRotation: new FormControl(4, [Validators.required]),
      coreSize: new FormControl(0.05, [Validators.required]),
      galaxyRadius: new FormControl(1500, [Validators.required]),
      // Voronoi cluster parameters
      clusterCount: new FormControl(80, [Validators.min(5), Validators.max(500)]),
      clusterSize: new FormControl(60, [Validators.min(10)]),
      clusterConcentration: new FormControl(0.7, [Validators.min(0), Validators.max(1)]),
      // Elliptical parameters
      sersicIndex: new FormControl(4.0, [Validators.min(0.5), Validators.max(10)]),
      axisRatio: new FormControl(0.7, [Validators.min(0.1), Validators.max(1)]),
      orientationAngle: new FormControl(0, [Validators.min(0), Validators.max(360)]),
      // Ring parameters
      ringRadius: new FormControl(900, [Validators.min(50)]),
      ringWidth: new FormControl(150, [Validators.min(10)]),
      ringIntensity: new FormControl(1.0, [Validators.min(0.1), Validators.max(2)]),
      coreToRingRatio: new FormControl(0.3, [Validators.min(0), Validators.max(1)]),
      // Irregular parameters
      irregularity: new FormControl(0.8, [Validators.min(0), Validators.max(1)]),
      irregularClumpCount: new FormControl(15, [Validators.min(5), Validators.max(50)]),
      irregularClumpSize: new FormControl(80, [Validators.min(20)]),
      // Noise texture parameters
      noiseOctaves: new FormControl(4, [Validators.required]),
      noisePersistence: new FormControl(0.5, [Validators.required]),
      noiseLacunarity: new FormControl(2.0, [Validators.required]),
      noiseScale: new FormControl(200, [Validators.required]),
      // Color parameters
      spaceBackgroundColor: new FormControl('#050510'),
      coreColor: new FormControl('#FFFADC'),
      armColor: new FormControl('#B4C8FF'),
      outerColor: new FormControl('#3C5078')
    });
  }

  onGalaxyTypeChange(): void {
    const galaxyType = this.galaxyForm.controls['galaxyType'].value;
    const spiralControls = ['numberOfArms', 'armWidth', 'armRotation'];
    const voronoiControls = ['clusterCount', 'clusterSize', 'clusterConcentration'];
    const ellipticalControls = ['sersicIndex', 'axisRatio', 'orientationAngle'];
    const ringControls = ['ringRadius', 'ringWidth', 'ringIntensity', 'coreToRingRatio'];
    const irregularControls = ['irregularity', 'irregularClumpCount', 'irregularClumpSize'];

    spiralControls.forEach(c => this.galaxyForm.controls[c].disable());
    voronoiControls.forEach(c => this.galaxyForm.controls[c].disable());
    ellipticalControls.forEach(c => this.galaxyForm.controls[c].disable());
    ringControls.forEach(c => this.galaxyForm.controls[c].disable());
    irregularControls.forEach(c => this.galaxyForm.controls[c].disable());

    if (galaxyType === 'SPIRAL') {
      spiralControls.forEach(c => this.galaxyForm.controls[c].enable());
    } else if (galaxyType === 'VORONOI_CLUSTER') {
      voronoiControls.forEach(c => this.galaxyForm.controls[c].enable());
    } else if (galaxyType === 'ELLIPTICAL') {
      ellipticalControls.forEach(c => this.galaxyForm.controls[c].enable());
    } else if (galaxyType === 'RING') {
      ringControls.forEach(c => this.galaxyForm.controls[c].enable());
    } else if (galaxyType === 'IRREGULAR') {
      irregularControls.forEach(c => this.galaxyForm.controls[c].enable());
    }
  }

  generateGalaxy(): void {
    if (this.galaxyForm.invalid) {
      this.snackBar.open('Please fill all required fields', 'Close', {duration: 3000});
      return;
    }

    this.isGenerating = true;
    this.generatedImageUrl = null; // Clear previous image
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

    const request: GalaxyRequestCmd = this.galaxyForm.value;
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
      numberOfArms: s.numberOfArms,
      armWidth: s.armWidth,
      armRotation: s.armRotation,
      coreSize: s.coreSize,
      galaxyRadius: s.galaxyRadius,
      clusterCount: s.clusterCount || 80,
      clusterSize: s.clusterSize || 60,
      clusterConcentration: s.clusterConcentration || 0.7,
      sersicIndex: s.sersicIndex || 4.0,
      axisRatio: s.axisRatio || 0.7,
      orientationAngle: s.orientationAngle || 0,
      ringRadius: s.ringRadius || 900,
      ringWidth: s.ringWidth || 150,
      ringIntensity: s.ringIntensity || 1.0,
      coreToRingRatio: s.coreToRingRatio || 0.3,
      irregularity: s.irregularity || 0.8,
      irregularClumpCount: s.irregularClumpCount || 15,
      irregularClumpSize: s.irregularClumpSize || 80,
      noiseOctaves: s.noiseOctaves,
      noisePersistence: s.noisePersistence,
      noiseLacunarity: s.noiseLacunarity,
      noiseScale: s.noiseScale,
      spaceBackgroundColor: s.spaceBackgroundColor || '#050510',
      coreColor: s.coreColor || '#FFFADC',
      armColor: s.armColor || '#B4C8FF',
      outerColor: s.outerColor || '#3C5078'
    });

    this.loadedGalaxyName = galaxy.name;
    this.onGalaxyTypeChange();
    this.generateGalaxy();
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
      case 'VORONOI_DEFAULT':
        this.galaxyForm.patchValue({
          clusterCount: 80,
          clusterSize: 60,
          clusterConcentration: 0.7,
          coreSize: 0.05,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'VORONOI_DENSE':
        this.galaxyForm.patchValue({
          clusterCount: 200,
          clusterSize: 40,
          clusterConcentration: 0.85,
          coreSize: 0.08,
          galaxyRadius: 1500,
          noiseOctaves: 5,
          noisePersistence: 0.6,
          noiseLacunarity: 2.2,
          noiseScale: 150
        });
        break;
      case 'VORONOI_SPARSE':
        this.galaxyForm.patchValue({
          clusterCount: 30,
          clusterSize: 90,
          clusterConcentration: 0.4,
          coreSize: 0.03,
          galaxyRadius: 1500,
          noiseOctaves: 3,
          noisePersistence: 0.4,
          noiseLacunarity: 1.8,
          noiseScale: 250
        });
        break;
      case 'ELLIPTICAL_DEFAULT':
        this.galaxyForm.patchValue({
          sersicIndex: 4.0,
          axisRatio: 0.7,
          orientationAngle: 45,
          coreSize: 0.05,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'ELLIPTICAL_ROUND':
        this.galaxyForm.patchValue({
          sersicIndex: 2.0,
          axisRatio: 0.95,
          orientationAngle: 0,
          coreSize: 0.08,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'ELLIPTICAL_FLAT':
        this.galaxyForm.patchValue({
          sersicIndex: 6.0,
          axisRatio: 0.4,
          orientationAngle: 30,
          coreSize: 0.04,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'RING_DEFAULT':
        this.galaxyForm.patchValue({
          ringRadius: 900,
          ringWidth: 150,
          ringIntensity: 1.0,
          coreToRingRatio: 0.3,
          coreSize: 0.05,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'RING_WIDE':
        this.galaxyForm.patchValue({
          ringRadius: 1000,
          ringWidth: 250,
          ringIntensity: 0.8,
          coreToRingRatio: 0.2,
          coreSize: 0.03,
          galaxyRadius: 1500,
          noiseOctaves: 4,
          noisePersistence: 0.5,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
      case 'RING_BRIGHT':
        this.galaxyForm.patchValue({
          ringRadius: 800,
          ringWidth: 120,
          ringIntensity: 1.2,
          coreToRingRatio: 0.5,
          coreSize: 0.08,
          galaxyRadius: 1500,
          noiseOctaves: 5,
          noisePersistence: 0.6,
          noiseLacunarity: 2.2,
          noiseScale: 180
        });
        break;
      case 'IRREGULAR_DEFAULT':
        this.galaxyForm.patchValue({
          irregularity: 0.8,
          irregularClumpCount: 15,
          irregularClumpSize: 80,
          coreSize: 0.03,
          galaxyRadius: 1500,
          noiseOctaves: 6,
          noisePersistence: 0.7,
          noiseLacunarity: 2.5,
          noiseScale: 150
        });
        break;
      case 'IRREGULAR_CHAOTIC':
        this.galaxyForm.patchValue({
          irregularity: 0.95,
          irregularClumpCount: 25,
          irregularClumpSize: 60,
          coreSize: 0.02,
          galaxyRadius: 1500,
          noiseOctaves: 8,
          noisePersistence: 0.8,
          noiseLacunarity: 3.0,
          noiseScale: 120
        });
        break;
      case 'IRREGULAR_DWARF':
        this.galaxyForm.patchValue({
          irregularity: 0.7,
          irregularClumpCount: 8,
          irregularClumpSize: 100,
          coreSize: 0.05,
          galaxyRadius: 1500,
          noiseOctaves: 5,
          noisePersistence: 0.6,
          noiseLacunarity: 2.0,
          noiseScale: 200
        });
        break;
    }
  }
}
