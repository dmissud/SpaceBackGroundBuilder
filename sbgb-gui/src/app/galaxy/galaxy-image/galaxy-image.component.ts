import {Component, Input} from '@angular/core';
import {ImagePreviewComponent} from "../../shared/components/image-preview/image-preview.component";

@Component({
  selector: 'app-galaxy-image',
  standalone: true,
  imports: [ImagePreviewComponent],
  template: `
    <app-image-preview
      [imageUrl]="imageUrl"
      [isGenerating]="isGenerating"
      emptyMessage="Aucune image générée"
      emptyHint="Cliquez sur &quot;Générer aperçu&quot; pour créer votre galaxie"
      altText="Generated galaxy image">
    </app-image-preview>
  `,
  styles: ``
})
export class GalaxyImageComponent {
  @Input() imageUrl: string | null = null;
  @Input() isGenerating: boolean = false;
}
