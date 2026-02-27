import {Component, ElementRef, HostListener, Input, ViewChild} from '@angular/core';
import {MatProgressSpinner} from "@angular/material/progress-spinner";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-image-preview',
  standalone: true,
  imports: [MatProgressSpinner, MatIconButton, MatIcon, MatTooltip],
  templateUrl: './image-preview.component.html',
  styleUrl: './image-preview.component.scss'
})
export class ImagePreviewComponent {
  @Input() imageUrl: string | ArrayBuffer | null = null;
  @Input() isGenerating: boolean = false;
  @Input() emptyMessage: string = 'Aucune image générée';
  @Input() emptyHint: string = 'Cliquez sur "Générer aperçu" pour créer votre image';
  @Input() altText: string = 'Generated image';

  @ViewChild('container') containerRef!: ElementRef<HTMLDivElement>;

  realSize = false;
  isFullscreen = false;

  @HostListener('document:fullscreenchange')
  onFullscreenChange(): void {
    this.isFullscreen = !!document.fullscreenElement;
  }

  toggleRealSize(): void {
    this.realSize = !this.realSize;
  }

  toggleFullscreen(): void {
    if (this.isFullscreen) {
      document.exitFullscreen();
    } else {
      this.containerRef.nativeElement.requestFullscreen();
    }
  }
}
