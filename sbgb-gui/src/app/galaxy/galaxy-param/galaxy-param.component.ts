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
