import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {SbgbImageComponent} from './sbgb-image/sbgb-image.component';
import {CommonModule} from "@angular/common";
import {MatFormField} from "@angular/material/form-field";
import {ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {HttpClientModule} from "@angular/common/http"; // mettez à jour le chemin d'accès

const routes: Routes = [
  { path: '', component: SbgbImageComponent}
];

@NgModule({
  declarations: [SbgbImageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    MatFormField,
    ReactiveFormsModule,
    MatButton,
    MatInput,
    HttpClientModule
    // importez les routes ici
  ],
  exports: [RouterModule, SbgbImageComponent]
})
export class SbgbModule { }
