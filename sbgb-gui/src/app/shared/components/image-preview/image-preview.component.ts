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

  isRealSize = false;
  isFullscreen = false;

  /** Synchronise l'état interne avec l'événement natif fullscreenchange du navigateur. */
  @HostListener('document:fullscreenchange')
  onFullscreenChange(): void {
    this.isFullscreen = !!document.fullscreenElement;
  }

  /** Bascule entre l'affichage ajusté à la fenêtre et la taille réelle (1:1 pixel). */
  toggleRealSize(): void {
    this.isRealSize = !this.isRealSize;
  }

  /** Bascule le mode plein écran. Respecte l'état isRealSize (scroll ou fit). */
  toggleFullscreen(): void {
    if (this.isFullscreen) {
      document.exitFullscreen();
    } else {
      this.containerRef?.nativeElement.requestFullscreen();
    }
  }
}
