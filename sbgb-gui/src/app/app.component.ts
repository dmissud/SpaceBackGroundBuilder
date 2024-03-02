import { Component } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {MatToolbar} from "@angular/material/toolbar";
import {MatAnchor, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";

import * as packageInfo from '../../package.json'
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MatToolbar, RouterLinkActive, MatAnchor, MatIcon, MatIconButton, RouterLink],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  title = 'sbgb-gui';
  get name(): string {
    return this._name;
  }
  get version(): string {
    return this._version;
  }

  private _name: string = packageInfo.name;
  private _version: string = packageInfo.version;

}
