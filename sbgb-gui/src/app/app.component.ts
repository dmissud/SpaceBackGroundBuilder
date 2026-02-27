import { Component, OnInit } from '@angular/core';
import {RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {MatToolbar} from "@angular/material/toolbar";
import {MatAnchor, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";
import {CommonModule} from "@angular/common";

import packageInfo from '../../package.json';
import {MatSnackBar} from "@angular/material/snack-bar";
import {ImagesService} from "./sbgb/images.service";
@Component({
    selector: 'app-root',
    imports: [RouterOutlet, MatToolbar, RouterLinkActive, MatAnchor, MatIcon, MatIconButton, RouterLink, CommonModule, MatTooltip],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  constructor(private _snackBar: MatSnackBar, private imagesService: ImagesService) {
  }

  ngOnInit() {
    this.imagesService.getActuatorInfo().subscribe({
      next: (info) => {
        this._version = info.app.version;
        this._branch = info.git.branch;
        this._sha = info.git.commit.id.abbrev;
      },
      error: (err) => {
        console.error('Could not fetch actuator info', err);
      }
    });
  }

  title = 'sbgb-gui';
  get name(): string {
    return this._name;
  }
  get version(): string {
    return this._version;
  }

  get branch(): string {
    return this._branch;
  }

  get sha(): string {
    return this._sha;
  }

  private _name: string = packageInfo['name'];
  private _version: string = packageInfo['version'];
  private _branch: string = '';
  private _sha: string = '';

}
