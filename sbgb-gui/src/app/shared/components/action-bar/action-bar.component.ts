import {Component, Input, Output, EventEmitter} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {MatTooltip} from "@angular/material/tooltip";
import {MatIcon} from "@angular/material/icon";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

export interface ActionBarButton {
  label: string;
  color?: 'primary' | 'accent' | 'warn';
  disabled: boolean;
  tooltip: string;
  action: () => void;
}

@Component({
  selector: 'app-action-bar',
  standalone: true,
  imports: [
    MatButton,
    MatTooltip,
    MatIcon,
    MatProgressSpinner
  ],
  templateUrl: './action-bar.component.html',
  styleUrl: './action-bar.component.scss'
})
export class ActionBarComponent {
  @Input() buttons: ActionBarButton[] = [];
  @Input() isGenerating: boolean = false;
  @Input() summary: string | null = null;
  @Input() generatingMessage: string = 'Génération en cours...';

  onButtonClick(button: ActionBarButton) {
    if (!button.disabled) {
      button.action();
    }
  }
}
