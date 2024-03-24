import {Component} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatSlider, MatSliderThumb} from "@angular/material/slider";
import {NgIf} from "@angular/common";
import {ReactiveFormsModule} from "@angular/forms";
import {SbgbParamComponent} from "../sbgb-param/sbgb-param.component";
import {SbgbImageComponent} from "../sbgb-image/sbgb-image.component";

@Component({
  selector: 'app-sbgb-shell',
  standalone: true,
  imports: [
    MatButton,
    MatCard,
    MatCardContent,
    MatFormField,
    MatInput,
    MatLabel,
    MatProgressSpinner,
    MatSlider,
    MatSliderThumb,
    NgIf,
    ReactiveFormsModule,
    SbgbParamComponent,
    SbgbImageComponent
  ],
  templateUrl: './sbgb-shell.component.html',
  styleUrl: './sbgb-shell.component.scss'
})
export class SbgbShellComponent {

}
