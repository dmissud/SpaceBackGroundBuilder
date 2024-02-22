import { Component } from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';

@Component({
  selector: 'app-sbgb-topmenu',
  standalone: true,
  imports: [MatToolbarModule, MatButtonModule, MatIconModule],
  templateUrl: './sbgb-topmenu.component.html',
  styleUrl: './sbgb-topmenu.component.scss'
})
export class SbgbTopmenuComponent {

}
