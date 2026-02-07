import {Component} from '@angular/core';
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {GalaxyParamComponent} from "../galaxy-param/galaxy-param.component";
import {GalaxyListComponent} from "../galaxy-list/galaxy-list.component";

@Component({
  selector: 'app-galaxy-shell',
  standalone: true,
  imports: [
    MatTabGroup,
    MatTab,
    GalaxyParamComponent,
    GalaxyListComponent
  ],
  templateUrl: './galaxy-shell.component.html',
  styleUrl: './galaxy-shell.component.scss'
})
export class GalaxyShellComponent {
}
