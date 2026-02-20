import {Component, Input} from '@angular/core';
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-galaxy-image',
  standalone: true,
  imports: [MatProgressSpinner],
  templateUrl: './galaxy-image.component.html',
  styleUrl: './galaxy-image.component.scss'
})
export class GalaxyImageComponent {
  @Input() imageUrl: string | null = null;
  @Input() isGenerating: boolean = false;
}
